name := "GraphRel"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions += "-target:jvm-1.8"

scalacOptions ++= Seq("-feature")

fork in run := true

javaOptions in run += "-Xmx2G"

libraryDependencies ++= Seq(
  "com.github.tototoshi" %% "scala-csv" % "1.3.3",
  "org.scalaj" % "scalaj-http_2.11" % "2.3.0",

  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "com.google.guava" % "guava" % "19.0",

  "it.unimi.dsi" % "webgraph" % "3.5.2",
  "it.unimi.dsi" % "fastutil" % "7.0.12",
  "it.unimi.dsi" % "sux4j" % "4.0.0",

  "edu.berkeley.nlp" % "berkeleylm" % "1.1.2",

  "org.apache.commons" % "commons-math3" % "3.6.1"

// http://www.scala-sbt.org/0.13.5/docs/Detailed-Topics/Library-Management.html
unmanagedJars in Compile ++= {
  val base = baseDirectory.value / "lib"
  val baseDirectories = (base / "law-2.3") +++ (base / "law-deps")
  val customJars = (baseDirectories ** "*.jar")
  customJars.classpath
}