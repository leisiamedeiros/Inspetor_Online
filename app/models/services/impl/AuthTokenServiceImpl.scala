package models.services.impl

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.util.Clock
import models.{ AuthToken, Usuario }
import models.daos.api.AuthTokenDAO
import models.services.api.AuthTokenService
import org.joda.time.DateTimeZone
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class AuthTokenServiceImpl @Inject() (
  authTokenDAO: AuthTokenDAO, clock: Clock
) extends AuthTokenService {
  def create(usuario: Usuario, expiry: FiniteDuration = 20 minutes) = {
    val token = AuthToken(
      UUID.randomUUID(),
      clock.now.withZone(DateTimeZone.UTC).plusSeconds(expiry.toSeconds.toInt),
      usuario.id
    )
    authTokenDAO.save(token)
  }

  def validate(id: UUID) = authTokenDAO.find(id)

  def clean = authTokenDAO.findExpired(
    clock.now.withZone(DateTimeZone.UTC)
  ).flatMap { tokens =>
      Future.sequence(tokens.map { token =>
        authTokenDAO.remove(token.id).map(_ => token)
      })
    }
}
