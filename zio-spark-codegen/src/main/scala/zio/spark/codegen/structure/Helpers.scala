package zio.spark.codegen.structure
import zio.spark.codegen.ScalaBinaryVersion
import zio.spark.codegen.generation.plan.SparkPlan

import scala.meta.*
import scala.util.Try

object Helpers {
  def cleanPrefixPackage(type_ : String): String =
    Try {
      def transformType(tpe: Type): Type =
        tpe match {
          case t"Array"                                 => t"Seq"
          case Type.Select(q"scala.collection", tpname) => t"collection.$tpname"
          case Type.Select(_, tpname: Type.Name)        => tpname
          case Type.Apply(tpe, args)                    => Type.Apply(transformType(tpe), args.map(transformType))
          case other                                    => other
        }

      val res = transformType(type_.parse[Type].get)
      res.toString()
    }.getOrElse("")

  def cleanType(type_ : String, plan: SparkPlan): String =
    cleanPrefixPackage(type_)
      .replaceAll(",\\b", ", ")
      .replace("this.type", s"${plan.name}${plan.template.typeParameter}")

  def checkMods(mods: List[Mod]): Boolean =
    !mods.exists {
      case mod"@DeveloperApi" => true
      case mod"private[$_]"   => true
      case mod"protected[$_]" => true
      case _                  => false
    }

  def collectFunctionsFromTemplate(template: TemplateWithComments): Seq[Defn.Def] =
    template.stats.collect { case d: Defn.Def if checkMods(d.mods) => d }

  def getTemplateFromSourceOverlay(source: Source): TemplateWithComments = {
    val template =
      source.children
        .collectFirst { case c: Defn.Class => c.templ }
        .orElse(source.children.flatMap(_.children).collectFirst { case c: Defn.Class => c.templ })

    template match {
      case Some(templ) => new TemplateWithComments(templ, true)
      case None        => throw new RuntimeException(s"Could not find class definition in source overlay")
    }
  }

  def getTemplateFromSource(source: Source): TemplateWithComments =
    new TemplateWithComments(
      source.children
        .flatMap(_.children) // First level: look for class directly in children
        .collectFirst { case c: Defn.Class => c.templ }
        .orElse(source.children.collectFirst { case c: Defn.Class => c.templ }) // Second level: look at top level
        .orElse {
          // Third level: Handle Spark 4 structure - look deeper into package structure
          source.children.flatMap { child =>
            child match {
              case pkg: Pkg =>
                // Look in package stats for classes
                pkg.stats
                  .collectFirst { case c: Defn.Class => c.templ }
                  .orElse {
                    // Look deeper - sometimes classes are nested further
                    pkg.stats.flatMap {
                      case nestedPkg: Pkg => nestedPkg.stats.collectFirst { case c: Defn.Class => c.templ }
                      case other          => other.children.collectFirst { case c: Defn.Class => c.templ }
                    }.headOption
                  }
              case _ =>
                // For non-package nodes, search recursively through all children
                def deepSearch(tree: Tree): Option[Template] =
                  tree match {
                    case c: Defn.Class => Some(c.templ)
                    case _             => tree.children.flatMap(deepSearch).headOption
                  }
                deepSearch(child)
            }
          }.headOption
        }
        .getOrElse {
          val allTypes    = source.children.map(_.getClass.getSimpleName)
          val nestedTypes = source.children.flatMap(_.children).map(_.getClass.getSimpleName)
          throw new RuntimeException(
            s"Could not find class definition in source. Top-level: [${allTypes.mkString(", ")}]. Nested: [${nestedTypes.mkString(", ")}]"
          )
        },
      false
    )

  def methodsFromSource(
      source: Source,
      filterOverlay: Boolean,
      hierarchy: String,
      className: String,
      scalaVersion: ScalaBinaryVersion
  ): Seq[Method] = {
    val template: TemplateWithComments =
      if (filterOverlay) getTemplateFromSourceOverlay(source)
      else getTemplateFromSource(source)

    val scalametaMethods = collectFunctionsFromTemplate(template)
    scalametaMethods.map(m => Method.fromScalaMeta(m, template.comments, hierarchy, className, scalaVersion))
  }
}
