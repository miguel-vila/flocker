name := "flocker"

version := "0.0.0"

scalaVersion := "2.11.2"

parallelExecution in Test := false

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",//para poder ver feature warnings al compilar
  "-language:postfixOps", //para cosas como '5 seconds'
  "-language:implicitConversions",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-language:reflectiveCalls", // para poder utilizar el .$each de la librería de mongodb
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
  //"-Ywarn-numeric-widen",
  //"-Ywarn-value-discard", // No muy buena idea combinar esto con akka
  "-Xfuture"
)

resolvers ++= Seq(
    "Sonatype Releases"   at "http://oss.sonatype.org/content/repositories/releases",
    "Sonatype Snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    "spray repo"          at "http://repo.spray.io",
    "clojars.org"         at "http://clojars.org/repo"
)

val scalazV = "7.0.6"
val sprayV = "1.3.1"
val akkaV = "2.3.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka"   %%  "akka-actor"                    % akkaV,
  "com.typesafe.akka"   %%  "akka-persistence-experimental" % akkaV,
  "com.github.ddevore"  %%  "akka-persistence-mongo-casbah" % "0.7.4",
  "org.twitter4j"       %   "twitter4j-core"                % "4.0.0",
  "org.scalaz"          %%  "scalaz-core"                   % scalazV,
  "io.spray"            %%  "spray-can"                     % sprayV,
  "io.spray"            %%  "spray-routing"                 % sprayV,
  "net.debasishg"       %%  "redisclient"                   % "2.13",
  "joda-time"           %   "joda-time"                     % "1.5.2",
  "com.novus"           %%  "salat"                         % "1.9.9",
  /*
    "org.json4s"          %%  "json4s-jackson"                % "3.2.11",
    "org.reactivemongo"   %%  "reactivemongo"                 % "0.10.5.0.akka23",
    "net.fehmicansaglam"  %%  "reactivemongo-extensions-bson" % "0.10.0.4",
  */
  "io.spray"            %%  "spray-testkit"                 % sprayV    % "test",
  "org.mockito"         %   "mockito-core"                  % "1.10.8"  % "test",
  "com.typesafe.akka"   %%  "akka-testkit"                  % akkaV     % "test",
  "junit"               %   "junit"                         % "4.10"    % "test",
  "org.scalatest"       %%  "scalatest"                     % "2.2.1"   % "test",
  "org.scalacheck"      %%  "scalacheck"                    % "1.11.6"  % "test",
  "de.flapdoodle.embed" %   "de.flapdoodle.embed.mongo"     % "1.43"    % "test",
  "redis.embedded"      %   "embedded-redis"                % "0.3"     % "test"
)