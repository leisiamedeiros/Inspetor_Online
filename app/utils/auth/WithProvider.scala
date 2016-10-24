package utils.auth

import com.mohiva.play.silhouette.api.{ Authenticator, Authorization }
import models.Usuario
import play.api.mvc.Request

import scala.concurrent.Future

case class WithProvider[A <: Authenticator](
  provider: String
) extends Authorization[Usuario, A] {
  override def isAuthorized[B](usuario: Usuario, authenticator: A)(
    implicit
    request: Request[B]
  ): Future[Boolean] = {
    Future.successful(usuario.loginInfo.providerID == provider)
  }
}
