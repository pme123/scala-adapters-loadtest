
val gatlinV = "2.3.1"
lazy val gatlinDependencies = Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlinV % Test
  , "io.gatling"            % "gatling-test-framework"    % gatlinV % Test
)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "pme123"
      , scalaVersion := "2.12.6"
      , version := "0.1.0-SNAPSHOT"
    ))
    , name := "scala-adapters-loadtest"
    , libraryDependencies ++= gatlinDependencies
  ).enablePlugins(GatlingPlugin)