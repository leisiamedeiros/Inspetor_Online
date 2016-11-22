package controllers

import java.util.UUID

import concurrent.Future
import language.postfixOps

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import javax.inject.Inject
import models.services.api.{ AuthTokenService, UsuarioService }
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.mailer.MailerClient
import play.api.mvc.Controller
import utils.auth.DefaultEnv

class AtivacaoContaController @Inject() (
    val messagesApi: MessagesApi,
    silhouette: Silhouette[DefaultEnv],
    usuarioService: UsuarioService,
    authTokenService: AuthTokenService,
    mailerClient: MailerClient,
    implicit val webJarAssets: WebJarAssets) extends Controller with I18nSupport {
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
