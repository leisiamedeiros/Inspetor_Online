package models.daos.api

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.Usuario

import scala.concurrent.Future

trait UsuarioDAO extends DAO {
  def find(loginInfo: LoginInfo): Future[Option[Usuario]]
  def find(id: UUID): Future[Option[Usuario]]
  def find(email: String): Future[Option[Usuario]]
  def save(usuario: Usuario): Future[Usuario]
}
