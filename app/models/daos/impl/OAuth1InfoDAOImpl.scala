package models.daos.impl

import concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.OAuth1Info
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import javax.inject.Inject
import models.daos.api.DAO
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class OAuth1InfoDAOImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider) extends DelegableAuthInfoDAO[OAuth1Info] with DAO {

  import driver.api._

  protected def oauth1InfoQuery(loginInfo: LoginInfo) = for {
    (bdLoginInfo, bdOAuth1Info) <- loginInfoQuery(loginInfo)
      .join(oauth1Infos).on(_.id === _.loginInfoId)
  } yield bdOAuth1Info

  protected def oauth1InfoSubQuery(loginInfo: LoginInfo) =
    oauth1Infos.filter(_.loginInfoId in loginInfoQuery(loginInfo).map(_.id))

  protected def addAction(loginInfo: LoginInfo, authInfo: OAuth1Info) =
    loginInfoQuery(loginInfo).result.head.flatMap { bdLoginInfo =>
      oauth1Infos +=
        BDOAuth1Info(
          0,
          authInfo.token,
          authInfo.secret,
          bdLoginInfo.id)
    }.transactionally

  protected def updateAction(loginInfo: LoginInfo, authInfo: OAuth1Info) =
    oauth1InfoSubQuery(loginInfo).
      map(bdOAuth1Info =>
        (bdOAuth1Info.token, bdOAuth1Info.secret))
      .update((authInfo.token, authInfo.secret))

  def find(loginInfo: LoginInfo): Future[Option[OAuth1Info]] = {
    db.run(oauth1InfoQuery(loginInfo)
      .result.headOption).map { bdOAuth1InfoOption =>
      bdOAuth1InfoOption.map(bdOAuth1Info =>
        OAuth1Info(bdOAuth1Info.token, bdOAuth1Info.secret))
    }
  }

  def add(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] =
    db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  def update(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] =
    db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)

  def save(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] = {
    val query = loginInfoQuery(loginInfo)
      .joinLeft(oauth1Infos).on(_.id === _.loginInfoId)
    val action = query.result.head.flatMap {
      case (bdLoginInfo, Some(bdOAuth1Info)) => updateAction(loginInfo, authInfo)
      case (bdLoginInfo, None) => addAction(loginInfo, authInfo)
    }
    db.run(action).map(_ => authInfo)
  }

  def remove(loginInfo: LoginInfo): Future[Unit] =
    db.run(oauth1InfoSubQuery(loginInfo).delete).map(_ => ())

}
