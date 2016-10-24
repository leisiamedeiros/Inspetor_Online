package models.daos.api

import java.util.UUID
import org.joda.time.DateTime

import models.{ AuthToken, Usuario }

import scala.concurrent.Future

trait AuthTokenDAO extends DAO {
  def find(id: UUID): Future[Option[AuthToken]]
  def findExpired(dateTime: DateTime): Future[Seq[AuthToken]]
  def save(token: AuthToken): Future[AuthToken]
  def remove(id: UUID): Future[Unit]
}
