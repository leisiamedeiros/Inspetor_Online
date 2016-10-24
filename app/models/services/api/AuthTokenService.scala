package models.services.api

import java.util.UUID

import models.{ AuthToken, Usuario }

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

trait AuthTokenService {
  def create(usuario: Usuario, expiry: FiniteDuration = 5 minutes): Future[AuthToken]
  def validate(id: UUID): Future[Option[AuthToken]]
  def clean: Future[Seq[AuthToken]]
}
