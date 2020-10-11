package ofatenko.api.models

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class TelegramResponse(ok: Boolean, result: Seq[UpdateInfoEvent])
object TelegramResponse {
  implicit val decoder: Decoder[TelegramResponse] = deriveDecoder[TelegramResponse]
  implicit val encoder: Encoder[TelegramResponse] = deriveEncoder[TelegramResponse]
}

case class UpdateInfoEvent(update_id: Long, message: Option[Message])
object UpdateInfoEvent {
  implicit val decoder: Decoder[UpdateInfoEvent] = deriveDecoder[UpdateInfoEvent]
  implicit val encoder: Encoder[UpdateInfoEvent] = deriveEncoder[UpdateInfoEvent]
}

case class Message(message_id: Long, from: Option[UserInfo], chat: ChatInfo, date: Long, text: Option[String])
object Message {
  implicit val decoder: Decoder[Message] = deriveDecoder[Message]
  implicit val encoder: Encoder[Message] = deriveEncoder[Message]
}

case class UserInfo(id: Long, is_bot: Boolean, first_name: String, username: String, language_code: Option[String])
object UserInfo {
  implicit val decoder: Decoder[UserInfo] = deriveDecoder[UserInfo]
  implicit val encoder: Encoder[UserInfo] = deriveEncoder[UserInfo]
}

case class ChatInfo(id: Long, first_name: String, username: String, `type`: String)
object ChatInfo {
  implicit val decoder: Decoder[ChatInfo] = deriveDecoder[ChatInfo]
  implicit val encoder: Encoder[ChatInfo] = deriveEncoder[ChatInfo]
}