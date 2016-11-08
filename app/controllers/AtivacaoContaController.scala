package controllers

import java.net.URLDecoder
import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.services.api.{ AuthTokenService, UsuarioService }
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.mvc.Controller

import scala.concurrent.Future
import scala.language.postfixOps

import utils.auth.DefaultEnv

class AtivacaoContaController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  usuarioService: UsuarioService,
  authTokenService: AuthTokenService,
  mailerClient: MailerClient,
  implicit val webJarAssets: WebJarAssets
) extends Controller with I18nSupport {
  def ativar(token: UUID) = silhouette.UnsecuredAction.async { implicit request =>
    authTokenService.validate(token).flatMap {
      case Some(authToken) => usuarioService.retrieve(authToken.usuarioID).flatMap {
        case Some(usuario) if usuario.loginInfo.providerID == CredentialsProvider.ID =>
          usuarioService.save(usuario.copy(ativado = true)).map { _ =>
            Redirect(routes.MainController.index())
          }
        case _ => Future.successful(Redirect(routes.MainController.index()).flashing("error" -> Messages("invalido.ativacao.link")))
      }
      case None => Future.successful(Redirect(routes.MainController.index()).flashing("error" -> Messages("invalido.ativacao.link")))
    }
  }
}
