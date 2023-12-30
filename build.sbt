import Dependencies.*

ThisBuild / organization := "com.samples"
ThisBuild / version      := "1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"
ThisBuild / scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature"
)

ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

lazy val foundations = (project in file("modules/foundations"))
  .settings(
    libraryDependencies ++= backendDeps
  )

lazy val server = (project in file("modules/server"))
  .settings(
    libraryDependencies ++= backendDeps
  )

lazy val root = (project in file("."))
  .settings(
    name := "zio-fullstack"
  )
  .aggregate(server)
  .dependsOn(server)
