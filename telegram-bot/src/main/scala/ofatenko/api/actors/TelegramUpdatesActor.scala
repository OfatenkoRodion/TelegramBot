package ofatenko.api.actors

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import ofatenko.api.models.{BotConfiguration, TelegramResponse}
import ofatenko.api.service.TelegramReactionService

import scala.concurrent.{ExecutionContext, Future}

object TelegramUpdatesActor {

  object Command {
    case class Offset(offset: Long)
    object CheckUpdates
    object Init
  }

}

class TelegramUpdatesActor(botConfiguration: BotConfiguration,
                           telegramReactionService: TelegramReactionService)
                          (implicit executionContext: ExecutionContext, mat: ActorSystem) extends Actor with LazyLogging {

  import TelegramUpdatesActor.Command

  override def receive: Receive = init

  private def init: Receive = {
    case Command.CheckUpdates =>
      context.become(lock)
      getUpdates
  }

  private def withOffset(offset: Long): Receive = {
    case Command.CheckUpdates =>
      context.become(lock)
      getUpdatesWithOffset(offset)
  }

  private def lock: Receive = {
    case Command.Offset(offset) => context.become(withOffset(offset))
    case Command.Init => context.become(init)
    case _ => ()
  }

  private def updates(offset: Long = 0): Future[TelegramResponse] = {
    for {
      httpResp <- Http().singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = s"https://api.telegram.org/bot${botConfiguration.token}/getUpdates?offset=${offset}&limit=${limit}",
      ))
      telegramResponse <- Unmarshal(httpResp).to[TelegramResponse]
      _ = logger.info(telegramResponse.result.map(_.message).toString())
      _ = telegramReactionService.reactTo(telegramResponse)
    } yield telegramResponse
  }

  private def getUpdates: Future[Unit] = {
    updates()
      .map {
        _.result.headOption match {
          case Some(v) => self ! Command.Offset(v.update_id + limit)
          case None => self ! Command.Init
        }
      }
      .recover {
        case th: Throwable =>
          self ! Command.Init
          logger.error("Ошибка чтения сообщения", th)
      }
  }

  private def getUpdatesWithOffset(offset: Long): Future[Unit] = {
    updates(offset)
      .map {
        _.result.headOption match {
          case Some(_) => self ! Command.Offset(offset + limit)
          case None => self ! Command.Offset(offset)
        }
      }
      .recover {
        case th: Throwable =>
          self ! Command.Offset(offset + limit)
          logger.error("Ошибка чтения сообщения", th)
      }
  }

  private val limit = 1

}
