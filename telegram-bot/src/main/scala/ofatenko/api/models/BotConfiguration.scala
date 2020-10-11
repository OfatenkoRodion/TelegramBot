package ofatenko.api.models

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

case class BotConfiguration(name: String, token: String)

object BotConfiguration {
  implicit val configReader: ConfigReader[BotConfiguration] = deriveReader[BotConfiguration]
}