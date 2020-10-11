
lazy val telegramBotVersion = "0.1"
scalaVersion := "2.13.3"

lazy val `telegram-bot-parent`: Project = project.in(file("."))
  .settings(skip in publish := true)
  .aggregate()

lazy val `telegram-bot`: Project = project
  .settings(
    name := "telegram-bot",
    version := telegramBotVersion,

    libraryDependencies ++= TelegramBot.dependencies,
    Compile / mainClass := Some("ofatenko.api.TelegramBotProject")
  )