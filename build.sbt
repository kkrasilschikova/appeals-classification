import sbtassembly.MergeStrategy

name := "appeals-classification"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

mainClass in assembly := Some("appeals_classification.Main")

assemblyOutputPath in assembly := file(s"./${name.value}.jar")

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.3",
  "org.apache.spark" %% "spark-sql" % "2.4.3",
  "org.apache.spark" %% "spark-mllib" % "2.4.3",
  "com.typesafe.akka" %% "akka-slf4j" % "2.6.0-M4",
  "com.typesafe.akka" %% "akka-http" % "10.1.9",
  "com.typesafe.akka" %% "akka-stream" % "2.6.0-M4",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.9",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case n if n.contains("services") => MergeStrategy.concat
  case x => MergeStrategy.first
}

val defaultMergeStrategy: String => MergeStrategy = {
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.deduplicate
    }
  case _ => MergeStrategy.deduplicate
}