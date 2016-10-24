package models.daos.impl

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.OpenIDInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import play.api.libs.concurrent.Execution.Implicits._
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future

import models.daos.api.DAO

class OpenIDInfoDAOImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) extends DelegableAuthInfoDAO[OpenIDInfo] with DAO {

  import driver.api._

  protected def openIDInfoQuery(loginInfo: LoginInfo) = for {
    (bdLoginInfo, bdOpenIDInfo) <- loginInfoQuery(loginInfo)
      .join(openIDInfos).on(_.id === _.loginInfoId)
  } yield bdOpenIDInfo

  protected def addAction(loginInfo: LoginInfo, authInfo: OpenIDInfo) =
    loginInfoQuery(loginInfo).result.head.flatMap { bdLoginInfo =>
      DBIO.seq(
        openIDInfos += BDOpenIDInfo(authInfo.id, bdLoginInfo.id),
        openIDAttributes ++= authInfo.attributes.map {
          case (key, value) => BDOpenIDAttribute(authInfo.id, key, value)
        }
      )
    }.transactionally

  protected def updateAction(loginInfo: LoginInfo, authInfo: OpenIDInfo) =
    openIDInfoQuery(loginInfo).result.head.flatMap { bdOpenIDInfo =>
      DBIO.seq(
        openIDInfos.filter(_.id === bdOpenIDInfo.id).update(
          bdOpenIDInfo.copy(id = authInfo.id)
        ),
        openIDAttributes.filter(_.id === bdOpenIDInfo.id).delete,
        openIDAttributes ++= authInfo.attributes.map {
          case (key, value) => BDOpenIDAttribute(authInfo.id, key, value)
        }
      )
    }.transactionally

  def find(loginInfo: LoginInfo): Future[Option[OpenIDInfo]] = {
    val query = openIDInfoQuery(loginInfo)
      .joinLeft(openIDAttributes).on(_.id === _.id)
    val result = db.run(query.result)
    result.map { openIDInfos =>
      if (openIDInfos.isEmpty) None
      else {
        val attrs = openIDInfos.collect {
          case (id, Some(attr)) => (attr.key, attr.value)
        }.toMap
        Some(OpenIDInfo(openIDInfos.head._1.id, attrs))
      }
    }
  }

  def add(loginInfo: LoginInfo, authInfo: OpenIDInfo): Future[OpenIDInfo] =
    db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  def update(loginInfo: LoginInfo, authInfo: OpenIDInfo): Future[OpenIDInfo] =
    db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)

  def save(loginInfo: LoginInfo, authInfo: OpenIDInfo): Future[OpenIDInfo] = {
    val query = loginInfoQuery(loginInfo)
      .joinLeft(openIDInfos).on(_.id === _.loginInfoId)
    val action = query.result.head.flatMap {
      case (bdLoginInfo, Some(bdOpenIDInfo)) => updateAction(loginInfo, authInfo)
      case (bdLoginInfo, None) => addAction(loginInfo, authInfo)
    }
    db.run(action).map(_ => authInfo)
  }

  def remove(loginInfo: LoginInfo): Future[Unit] = {
    val openIDInfoSubQuery = openIDInfos
      .filter(_.loginInfoId in loginInfoQuery(loginInfo).map(_.id))
    val attributeSubQuery = openIDAttributes
      .filter(_.id in openIDInfoSubQuery.map(_.id))
    db.run((openIDInfoSubQuery.delete andThen attributeSubQuery.delete)
      .transactionally).map(_ => ())
  }

}
