package utils.auth

import com.mohiva.play.silhouette.api.{ Authenticator, Authorization }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

import models.Usuario
import play.api.mvc.Request

import scala.concurrent.Future

case class WithRole(roles: List[String]) extends Authorization[Usuario, CookieAuthenticator] {

  override def isAuthorized[B](usuario: Usuario, authenticator: CookieAuthenticator)(
    implicit
    request: Request[B]
  ): Future[Boolean] = {
    Future.successful(roles.contains(usuario.papel))
  }
}
