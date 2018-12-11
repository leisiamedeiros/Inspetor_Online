package models.daos.impl

import java.util.UUID
import concurrent.Future
import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.Inject
import models.Usuario
import models.daos.api.{ UsuarioDAO }
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class UsuarioDAOImpl @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider) extends UsuarioDAO with DAO {

  import driver.api._

  def find(loginInfo: LoginInfo): Future[Option[Usuario]] = {
    val query = loginInfoQuery(loginInfo)
      .join(usuarioLoginInfos).on(_.id === _.loginInfoId)
      .join(usuarios).on(_._2.usuarioID === _.id)
      .result.headOption
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case ((loginInfoRow, usuarioLoginInfoRow), usuarioRow) =>
          Usuario(
            usuarioRow.id,
            loginInfo,
            usuarioRow.papel,
            usuarioRow.nomeCompleto,
            usuarioRow.email,
            usuarioRow.avatarURL,
            usuarioRow.ativado)
      }
    }
    db.run(action)
  }
  def find(id: UUID): Future[Option[Usuario]] = {
    val query = usuarios.filter(_.id === id)
      .join(usuarioLoginInfos).on(_.id === _.usuarioID)
      .join(loginInfos).on(_._2.loginInfoId === _.id)
      .result.headOption
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case ((usuarioRow, usuarioLoginInfoRow), loginInfoRow) =>
          Usuario(
            usuarioRow.id,
            new LoginInfo(loginInfoRow.providerID, loginInfoRow.providerKey),
            usuarioRow.papel,
            usuarioRow.nomeCompleto,
            usuarioRow.email,
            usuarioRow.avatarURL,
            usuarioRow.ativado)
      }
    }
    db.run(action)
  }
  def find(email: String): Future[Option[Usuario]] = {
    val query = usuarios.filter(_.email === email)
      .join(usuarioLoginInfos).on(_.id === _.usuarioID)
      .join(loginInfos).on(_._2.loginInfoId === _.id)
      .result.headOption
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case ((usuarioRow, usuarioLoginInfoRow), loginInfoRow) =>
          Usuario(
            usuarioRow.id,
            new LoginInfo(loginInfoRow.providerID, loginInfoRow.providerKey),
            usuarioRow.papel,
            usuarioRow.nomeCompleto,
            usuarioRow.email,
            usuarioRow.avatarURL,
            usuarioRow.ativado
          )
      }
    }
    db.run(action)
  }
  def save(usuario: Usuario): Future[Usuario] = {
    val bdUsuario = BDUsuario(
      usuario.id,
      usuario.papel,
      usuario.nomeCompleto,
      usuario.email,
      usuario.avatarURL,
      usuario.ativado)
    val bdLoginInfo = BDLoginInfo(
      0,
      usuario.loginInfo.providerID,
      usuario.loginInfo.providerKey)
    val loginInfoAction = {
      val retrieveLoginInfo = loginInfos.filter(
        info => info.providerID === usuario.loginInfo.providerID &&
          info.providerKey === usuario.loginInfo.providerKey).result.headOption

      val insertLoginInfo = loginInfos.returning(loginInfos.map(_.id))
        .into((info, id) => info.copy(id = id)) += bdLoginInfo

      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful(_)).getOrElse(insertLoginInfo)
      } yield loginInfo
    }

    val actions = (for {
      _ <- usuarios.insertOrUpdate(bdUsuario)
      loginInfo <- loginInfoAction
      _ <- usuarioLoginInfos += BDUsuarioLoginInfo(bdUsuario.id, loginInfo.id)
    } yield ()).transactionally
    db.run(actions).map(_ => usuario)
  }

}
