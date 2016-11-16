package models.daos.impl

import concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import javax.inject.Inject
import models.daos.api.DAO
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class OAuth2InfoDAOImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider) extends DelegableAuthInfoDAO[OAuth2Info] with DAO {

  import driver.api._

  protected def oauth2InfoQuery(loginInfo: LoginInfo) = for {
    (bdLoginInfo, bdOAuth2Info) <- loginInfoQuery(loginInfo)
      .join(oauth2Infos).on(_.id === _.loginInfoId)
  } yield bdOAuth2Info

  protected def oauth2InfoSubQuery(loginInfo: LoginInfo) =
    oauth2Infos.filter(_.loginInfoId in loginInfoQuery(loginInfo).map(_.id))

  protected def addAction(loginInfo: LoginInfo, authInfo: OAuth2Info) =
    loginInfoQuery(loginInfo).result.head.flatMap { bdLoginInfo =>
      oauth2Infos +=
        BDOAuth2Info(
          0,
          authInfo.accessToken,
          authInfo.tokenType,
          authInfo.expiresIn,
          authInfo.refreshToken,
          bdLoginInfo.id)
    }.transactionally

  protected def updateAction(loginInfo: LoginInfo, authInfo: OAuth2Info) =
    oauth2InfoSubQuery(loginInfo).
      map(bdOAuth2Info => (
        bdOAuth2Info.accessToken,
        bdOAuth2Info.tokenType,
        bdOAuth2Info.expiresIn,
        bdOAuth2Info.refreshToken))
      .update((
        authInfo.accessToken,
        authInfo.tokenType,
        authInfo.expiresIn,
        authInfo.refreshToken))

  def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] = {
    db.run(oauth2InfoQuery(loginInfo)
      .result.headOption).map { bdOAuth2InfoOption =>
      bdOAuth2InfoOption.map(bdOAuth2Info =>
        OAuth2Info(
          bdOAuth2Info.accessToken,
          bdOAuth2Info.tokenType,
          bdOAuth2Info.expiresIn,
          bdOAuth2Info.refreshToken))
    }
  }

  def add(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] =
    db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  def update(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] =
    db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)

  def save(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = {
    val query = loginInfoQuery(loginInfo)
      .joinLeft(oauth2Infos).on(_.id === _.loginInfoId)
    val action = query.result.head.flatMap {
      case (bdLoginInfo, Some(bdOAuth2Info)) => updateAction(loginInfo, authInfo)
      case (bdLoginInfo, None) => addAction(loginInfo, authInfo)
    }
    db.run(action).map(_ => authInfo)
  }

  def remove(loginInfo: LoginInfo): Future[Unit] =
    db.run(oauth2InfoSubQuery(loginInfo).delete).map(_ => ())

}
