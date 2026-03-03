# Spark 4.1.1 Migration — What Was Done

## Context

Apache Spark 4 restructured its SQL module: implementation classes (`Dataset`, `DataFrameNaFunctions`, `DataFrameStatFunctions`, `KeyValueGroupedDataset`, `RelationalGroupedDataset`) moved from `org.apache.spark.sql` to `org.apache.spark.sql.classic` in the source JAR. Additionally, Spark 4 introduced a separate `spark-sql-api` module containing abstract API definitions (e.g., abstract `Dataset`), while the concrete implementations remain in `spark-sql`. zio-spark's codegen reads Spark source JARs to auto-generate ZIO wrappers, so both changes needed to be addressed.

Spark 4 also dropped Scala 2.12 support (only 2.13+), which affected the codegen sbt plugin (sbt 1.x plugins must be Scala 2.12).

---

## Step 1: Version Bumps

- `build.sbt`: `sparkVersion` `"3.5.4"` → `"4.1.1"`, `spark-scala3-encoders` → `spark4-scala3-encoders`, added `spark-sql-api` dependency
- `zio-spark-codegen/build.sbt`: `sparkVersion` `"3.5.8"` → `"4.1.1"`, changed Spark deps from `%%` to explicit `_2.13` with `.intransitive()` to avoid Scala 2.12/2.13 cross-version conflicts
- 5 example `build.sbt` files: `"3.5.8"` → `"4.1.1"`

## Step 2: Codegen — Source Path Fix + API Module Merging

### `Module.scala`
- Added `sourceSubPath: Option[String]` field — `sqlModule` uses `Some("classic")` to redirect source lookup to `org/apache/spark/sql/classic/`
- Added `apiModule: Option[(String, String)]` field — `sqlModule` uses `Some(("spark-sql-api", "org/apache/spark/sql"))` to also read abstract API definitions
- Added `sourcePath` method combining `path` and `sourceSubPath`

### `SparkPlan.scala`
- Changed `module.path` → `module.sourcePath` for source JAR lookup
- Added `getSparkMethods` method that reads from both the implementation module (`spark-sql/classic/`) and the API module (`spark-sql-api/`), merging methods and deduplicating by `(name, arity)`
- Added `unsupportedTypes` filter for Spark 4 types not yet wrapped: `StatefulProcessor`, `StatefulProcessorWithInitialState`, `TimeMode`, `ReadOnlySparkConf`, `MergeIntoWriter`, `TableArg`, `BloomFilter`, `CountMinSketch`
- Changed Array/Java filters to use `rawSignature` (pre-cleaning) instead of `signature` (post-cleaning), since `cleanPrefixPackage` converts `Array` → `Seq` which would bypass the Array filter

## Step 3: Codegen — Robust Source Parsing

### `Helpers.scala`
- Made class search recursive via a `findClass` helper to handle deeper package nesting in Spark 4's source JARs (e.g., `org.apache.spark.sql.classic.Dataset` has an extra package level)

## Step 4: Parameter Type Cleaning

### `Parameter.scala`
- Added `rawSignature` field preserving the original type string (before cleaning)
- Applied `Helpers.cleanPrefixPackage` to compute `signature` from `rawSignature` — previously parameters kept fully-qualified types like `sql.Dataset[_]` which caused compilation errors in generated code

## Step 5: Sniffer.scala

### `Sniffer.scala`
- Updated for Spark 4's abstract/concrete `Dataset` split: imports `classic.Dataset` and casts through it to access `showString`

## Step 6: Removed Spark 4 Dropped APIs

### `DataFrameStatFunctionsTemplate.scala`
- Removed `BloomFilter` import and `bloomFilter`/`countMinSketch` from method type overrides (both removed in Spark 4)

### `MethodType.scala`
- Removed `countMinSketch` and `bloomFilter` from `methodsGet` set

## Step 7: Test Updates

### Codegen tests
- `MethodTypeSpec.scala`: Changed `countMinSketch` test to `corr` (countMinSketch removed in Spark 4)
- `MethodSpec.scala`: Updated cogroup test assertion from `"other.underlying"` to `"unpack(_.cogroup"` (Spark 4 API change)

### Core tests
- `DataFrameReaderCompatibilitySpec.scala`: Removed `"userSpecifiedSchema"` from `allowedNewMethods` (now implemented via API merge)
- `DataFrameWriterCompatibilitySpec.scala`: Removed `"partitioningColumns"` from `allowedNewMethods` (now implemented via API merge)

---

## Files Modified (23 total)

| File | Change |
|------|--------|
| `build.sbt` | Spark 4.1.1, spark4-scala3-encoders, spark-sql-api dep |
| `zio-spark-codegen/build.sbt` | Spark 4.1.1, explicit _2.13 + intransitive |
| `examples/*/build.sbt` (5 files) | Spark 4.1.1 |
| `Module.scala` | sourceSubPath, apiModule for classic + API lookup |
| `SparkPlan.scala` | sourcePath, API merge, unsupportedTypes, rawSignature filters |
| `Helpers.scala` | Recursive findClass |
| `Parameter.scala` | rawSignature + cleanPrefixPackage |
| `Sniffer.scala` | classic.Dataset cast |
| `DataFrameStatFunctionsTemplate.scala` | Remove BloomFilter/countMinSketch |
| `MethodType.scala` | Remove countMinSketch/bloomFilter from methodsGet |
| `MethodTypeSpec.scala` | countMinSketch → corr test |
| `MethodSpec.scala` | cogroup assertion update |
| `DataFrameReaderCompatibilitySpec.scala` | Remove userSpecifiedSchema from allowed |
| `DataFrameWriterCompatibilitySpec.scala` | Remove partitioningColumns from allowed |
| Generated `scala-3/` files (5 files) | Auto-regenerated by codegen |

## Verification Results

- 32 codegen tests pass
- 150 core tests pass (2 ignored)
- Formatting clean (`fmtCheck` passes)
