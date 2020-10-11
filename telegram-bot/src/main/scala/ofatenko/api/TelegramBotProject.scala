package ofatenko.api

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}
import ofatenko.api.models.BotConfiguration

import ofatenko.api.actors.TelegramUpdatesActor
import ofatenko.api.service.TelegramReactionMockServiceImpl
import pureconfig.ConfigSource

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object TelegramBotProject extends App {

  val config: Config = ConfigFactory.load()
  val configSource: ConfigSource = ConfigSource.fromConfig(config)
  val botConfiguration = configSource.at("bot").loadOrThrow[BotConfiguration]

  implicit val context = ExecutionContext.global
  implicit val actorSystem = ActorSystem.create("ActorSystem")

  val telegramReactionService = new TelegramReactionMockServiceImpl(botConfiguration)

  val telegramUpdatesActor = actorSystem.actorOf(Props(new TelegramUpdatesActor(botConfiguration, telegramReactionService)))

  actorSystem.scheduler.scheduleAtFixedRate(0 seconds, 1000 milliseconds, telegramUpdatesActor, TelegramUpdatesActor.Command.CheckUpdates)
}