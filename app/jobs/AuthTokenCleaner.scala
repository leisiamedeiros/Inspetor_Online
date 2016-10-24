package jobs

import javax.inject.Inject

import akka.actor._
import com.mohiva.play.silhouette.api.util.Clock
import jobs.AuthTokenCleaner.Clean
import models.services.api.AuthTokenService
import utils.Logger

import scala.concurrent.ExecutionContext.Implicits.global

class AuthTokenCleaner @Inject() (
  service: AuthTokenService,
  clock: Clock
) extends Actor with Logger {
  def receive: Receive = {
    case Clean =>
      val start = clock.now.getMillis
      val msg = new StringBuffer("\n")
      msg.append("=================================================\n")
      msg.append("Começando a limpeza dos tokens de autenticação\n")
      msg.append("=================================================\n")
      service.clean.map { deleted =>
        val seconds = (clock.now.getMillis - start) / 1000
        msg.append("Total de %s tokens foram deletados em %s segundos"
          .format(deleted.length, seconds)).append("\n")
        msg.append("=================================================\n")
        logger.info(msg.toString)
      }.recover {
        case e =>
          msg.append("Não foi possível limpar os tokens, por conta de um erro não esperado!\n")
          msg.append("=================================================\n")
          logger.error(msg.toString, e)
      }
  }
}

object AuthTokenCleaner {
  case object Clean
}
