import sbt._
import sbt.librarymanagement.syntax.Test

trait BaseDependenciesSources {
  def dependencies: Seq[ModuleID]
}

trait DependenciesBase {
  def dependencies: Seq[ModuleID]

  def +(otherDependencies: DependenciesBase): Seq[ModuleID] = dependencies ++ otherDependencies.dependencies
}

class MainDependencies(val dependencies: ModuleID*) extends DependenciesBase
class TestDependencies(_dependencies: ModuleID*) extends DependenciesBase {
  val dependencies = _dependencies.map(_ % Test)
}

object TelegramBot extends BaseDependenciesSources {

  override def dependencies: Seq[ModuleID] =
    new MainDependencies(
      "com.typesafe" % "config" % "1.4.0",
      "com.github.pureconfig" %% "pureconfig" % "0.14.0",

      "com.typesafe.akka" %% "akka-http" % "10.2.0",
      "com.typesafe.akka" %% "akka-stream" % "2.6.9",
      "com.typesafe.akka" %% "akka-actor" % "2.6.9",

      "io.circe" %% "circe-generic" % "0.13.0",
      "de.heikoseeberger" %% "akka-http-circe" % "1.27.0" withSources(),

      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.slf4j" % "slf4j-api" % "1.7.30",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
    ) +
    new TestDependencies(
    )
}