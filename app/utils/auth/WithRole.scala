package utils.auth

import concurrent.Future

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

import models.Usuario
import play.api.mvc.Request

case class WithRole(role: String) extends Authorization[Usuario, CookieAuthenticator] {

  override def isAuthorized[B](usuario: Usuario, authenticator: CookieAuthenticator)(
    implicit
    request: Request[B]): Future[Boolean] = {
    Future.successful(usuario.papel == role)
  }
}
