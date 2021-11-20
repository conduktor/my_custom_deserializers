name                                     := "my_custom_deserializers"
version                                  := sys.env.getOrElse("CREATED_TAG", "0.1")
scalaVersion                             := "2.13.7"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.0.0"

// ## Github Packages publish configs
// More info, see: https://gist.github.com/guizmaii/2ca47b74ad8e26c772d7df6ada8ddb00
val GITHUB_OWNER   = "conduktor"
val GITHUB_PROJECT = "my_custom_deserializers"

ThisBuild / publishTo         := Some(
  s"GitHub $GITHUB_OWNER Apache Maven Packages" at s"https://maven.pkg.github.com/$GITHUB_OWNER/$GITHUB_PROJECT"
)
ThisBuild / publishMavenStyle := true
ThisBuild / credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  GITHUB_OWNER,
  System.getenv("GITHUB_TOKEN")
)
