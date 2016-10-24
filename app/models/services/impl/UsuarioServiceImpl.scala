package models.services.impl

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile

import models.Usuario
import models.daos.api.UsuarioDAO
import models.services.api.UsuarioService

import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class UsuarioServiceImpl @Inject() (usuarioDAO: UsuarioDAO) extends UsuarioService {
  def retrieve(id: UUID) = usuarioDAO.find(id)
  def retrieve(loginInfo: LoginInfo): Future[Option[Usuario]] =
    usuarioDAO.find(loginInfo)
  def save(usuario: Usuario) = usuarioDAO.save(usuario)
  def save(profile: CommonSocialProfile) = {
    usuarioDAO.find(profile.loginInfo).flatMap {
      case Some(usuario) =>
        usuarioDAO.save(usuario.copy(
          nomeCompleto = profile.fullName.getOrElse(usuario.nomeCompleto),
          email = profile.email.getOrElse(usuario.email),
          avatarURL = profile.avatarURL
        ))
      case None =>
        usuarioDAO.save(Usuario(
          id = UUID.randomUUID(),
          papel = "aluno",
          loginInfo = profile.loginInfo,
          nomeCompleto = profile.fullName.get,
          email = profile.email.get,
          avatarURL = profile.avatarURL,
          ativado = false
        ))
    }
  }
}
