package ofatenko.api.service

import java.net.URLEncoder

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.scalalogging.LazyLogging
import ofatenko.api.models.{BotConfiguration, TelegramResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait TelegramReactionService {

  def reactTo(to: TelegramResponse): Future[Unit]
}

class TelegramReactionMockServiceImpl(botConfiguration: BotConfiguration)
                                 (implicit executionContext: ExecutionContext, mat: ActorSystem) extends TelegramReactionService with LazyLogging {

  override def reactTo(telegramResponse: TelegramResponse): Future[Unit] = {
    val messages = telegramResponse.result.flatMap(v => v.message)

    Future.sequence(messages.map { msg =>
      val text =
        Try(if (msg.text.get == "/start") {
        "Прикажи мне!"
      } else {
        s"Я понял тебя моя зайка, ${msg.from.get.first_name}. ${msg.text.get} будет исполнено!"
      }).toOption.getOrElse("Это ты быканул?")



        for {
          httpResp <- Http().singleRequest(HttpRequest(
            method = HttpMethods.GET,
            uri = s"https://api.telegram.org/bot${botConfiguration.token}/sendMessage?chat_id=${msg.chat.id}&text=${URLEncoder.encode(text, "UTF-8")}",
          ))
          str <- Unmarshal(httpResp).to[String]
          _ = logger.info(str)
        } yield {}
      }
    ).map(_ => {})
  }

}
