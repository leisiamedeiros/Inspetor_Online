package models.services.api

import java.util.UUID

import concurrent.Future
import concurrent.duration.{ DurationInt, FiniteDuration }
import language.postfixOps

import models.{ AuthToken, Usuario }

trait AuthTokenService {
  def create(usuario: Usuario, expiry: FiniteDuration = 5 minutes): Future[AuthToken]
  def validate(id: UUID): Future[Option[AuthToken]]
  def clean: Future[Seq[AuthToken]]
}
