package models.daos.impl

import java.util.UUID

import concurrent.Future

import org.joda.time.DateTime

import javax.inject.Inject
import models.AuthToken
import models.daos.api.AuthTokenDAO
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class AuthTokenDAOImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider) extends AuthTokenDAO {

  import driver.api._

  def find(id: UUID): Future[Option[AuthToken]] = {
    val query = authTokens.filter(_.id === id).result.headOption
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case (authTokenRow) =>
          AuthToken(
            authTokenRow.id,
            authTokenRow.expiry,
            authTokenRow.usuarioID)
      }
    }
    db.run(action)
  }

  def findExpired(dateTime: DateTime): Future[Seq[AuthToken]] = {
    val query = authTokens.filter(_.expiry <= dateTime).result
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case (authTokenRow) =>
          AuthToken(
            authTokenRow.id,
            authTokenRow.expiry,
            authTokenRow.usuarioID)
      }
    }
    db.run(action)
  }

  def save(token: AuthToken): Future[AuthToken] = {
    val bdAuthToken = BDAuthToken(
      token.id,
      token.usuarioID,
      token.expiry)
    val query = authTokens.returning(authTokens.map(_.id))
    val action = (query += bdAuthToken).transactionally
    db.run(action).map(_ => token)
  }

  def remove(id: UUID): Future[Unit] = {
    val query = authTokens.filter(_.id === id)
    val action = query.delete
    db.run(action).map(_ => ())
  }
}
