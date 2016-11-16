package models.daos.api

import java.util.UUID

import concurrent.Future

import org.joda.time.DateTime

import models.AuthToken

trait AuthTokenDAO extends DAO {
  def find(id: UUID): Future[Option[AuthToken]]
  def findExpired(dateTime: DateTime): Future[Seq[AuthToken]]
  def save(token: AuthToken): Future[AuthToken]
  def remove(id: UUID): Future[Unit]
}
