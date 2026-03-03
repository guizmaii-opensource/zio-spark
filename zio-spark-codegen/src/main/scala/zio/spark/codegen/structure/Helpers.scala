package zio.spark.codegen.structure
import zio.spark.codegen.ScalaBinaryVersion
import zio.spark.codegen.generation.plan.SparkPlan

import scala.meta.*
import scala.util.Try

object Helpers {
  def cleanPrefixPackage(type_ : String): String =
    Try {
      val res =
        type_
          .parse[Type]
          .get
          .transform {
            case t"Array"                                 => t"Seq"
            case Type.Select(q"scala.collection", tpname) => t"collection.$tpname"
            case t"$ref.$tpname"                          => tpname
          }

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

  private def findClass(trees: List[Tree]): Option[Defn.Class] =
    trees.collectFirst { case c: Defn.Class => c } orElse
      trees.flatMap(t => findClass(t.children)).headOption

  def getTemplateFromSourceOverlay(source: Source): TemplateWithComments =
    new TemplateWithComments(
      findClass(source.children)
        .map(_.templ)
        .getOrElse(throw new NoSuchElementException("No class found in overlay source")),
      true
    )

  def getTemplateFromSource(source: Source): TemplateWithComments =
    new TemplateWithComments(
      findClass(source.children)
        .map(_.templ)
        .getOrElse(throw new NoSuchElementException("No class found in source")),
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
