import sbt.*

object Dependencies {
  // versions
  lazy val zioVersion        = "2.0.19"
  lazy val zioLoggingVersion = "2.1.12"
  lazy val zioConfigVersion  = "3.0.7"
  lazy val tapirVersion      = "1.9.2"
  lazy val sttpVersion       = "3.8.13"
  lazy val javaMailVersion   = "1.6.2"
  lazy val stripeVersion     = "24.3.0"

  // libraries
  val zioJson    = "dev.zio"                       %% "zio-json"    % "0.5.0"
  val zioPrelude = "dev.zio"                       %% "zio-prelude" % "1.0.0-RC16"
  val sttp       = "com.softwaremill.sttp.client3" %% "zio"         % sttpVersion
  val jwt        = "com.auth0"                      % "java-jwt"    % "4.3.0"
  val javaMail   = "com.sun.mail"                   % "javax.mail"  % javaMailVersion
  val stripeJava = "com.stripe"                     % "stripe-java" % stripeVersion

  val zioConfig = Seq(
    "dev.zio" %% "zio-config"          % zioConfigVersion,
    "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
    "dev.zio" %% "zio-config-typesafe" % zioConfigVersion
  )

  val persistence = Seq(
    "io.getquill"   %% "quill-jdbc-zio" % "4.7.3",
    "org.postgresql" % "postgresql"     % "42.5.4",
    "org.flywaydb"   % "flyway-core"    % "9.16.0"
  )

  val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-client"       % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-zio"          % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-zio"               % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"   % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server"  % tapirVersion % "test"
  )

  val logging = Seq(
    "dev.zio"       %% "zio-logging"       % zioLoggingVersion,
    "dev.zio"       %% "zio-logging-slf4j" % zioLoggingVersion,
    "ch.qos.logback" % "logback-classic"   % "1.4.7"
  )

  val testing = Seq(
    "dev.zio"               %% "zio-test"                          % zioVersion,
    "dev.zio"               %% "zio-test-junit"                    % zioVersion  % "test",
    "dev.zio"               %% "zio-test-sbt"                      % zioVersion  % "test",
    "dev.zio"               %% "zio-test-magnolia"                 % zioVersion  % "test",
    "dev.zio"               %% "zio-mock"                          % "1.0.0-RC9" % "test",
    "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % "0.10.0"
  )

  // projects
  val backendDeps = Seq(
    zioJson,
    zioPrelude,
    sttp,
    jwt,
    javaMail,
    stripeJava
  ) ++ zioConfig ++ persistence ++ tapir ++ logging ++ testing
}
