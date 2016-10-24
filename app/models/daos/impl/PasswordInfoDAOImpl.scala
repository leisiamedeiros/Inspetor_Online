package models.daos.impl

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import play.api.libs.concurrent.Execution.Implicits._
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future

import models.daos.api.DAO

class PasswordInfoDAOImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) extends DelegableAuthInfoDAO[PasswordInfo] with DAO {

  import driver.api._

  protected def passwordInfoQuery(loginInfo: LoginInfo) = for {
    (bdLoginInfo, bdPasswordInfo) <- loginInfoQuery(loginInfo)
      .join(passwordInfos).on(_.id === _.loginInfoId)
  } yield bdPasswordInfo

  protected def passwordInfoSubQuery(loginInfo: LoginInfo) =
    passwordInfos.filter(_.loginInfoId in loginInfoQuery(loginInfo).map(_.id))

  protected def addAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    loginInfoQuery(loginInfo).result.head.flatMap { bdLoginInfo =>
      passwordInfos +=
        BDPasswordInfo(
          authInfo.hasher,
          authInfo.password,
          authInfo.salt,
          bdLoginInfo.id
        )
    }.transactionally

  protected def updateAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    passwordInfoSubQuery(loginInfo).
      map(bdPasswordInfo =>
        (bdPasswordInfo.hasher, bdPasswordInfo.password, bdPasswordInfo.salt))
      .update((authInfo.hasher, authInfo.password, authInfo.salt))

  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    db.run(passwordInfoQuery(loginInfo)
      .result.headOption).map { bdPasswordInfoOption =>
      bdPasswordInfoOption.map(bdPasswordInfo =>
        PasswordInfo(bdPasswordInfo.hasher, bdPasswordInfo.password, bdPasswordInfo.salt)
      )
    }
  }

  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val query = loginInfoQuery(loginInfo)
      .joinLeft(passwordInfos).on(_.id === _.loginInfoId)
    val action = query.result.head.flatMap {
      case (bdLoginInfo, Some(bdPasswordInfo)) => updateAction(loginInfo, authInfo)
      case (bdLoginInfo, None) => addAction(loginInfo, authInfo)
    }
    db.run(action).map(_ => authInfo)
  }

  def remove(loginInfo: LoginInfo): Future[Unit] =
    db.run(passwordInfoSubQuery(loginInfo).delete).map(_ => ())

}
