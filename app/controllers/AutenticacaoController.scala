package controllers

import concurrent.Future
import concurrent.duration.FiniteDuration
import language.postfixOps

import com.mohiva.play.silhouette.api.{ LoginEvent, Silhouette }
import com.mohiva.play.silhouette.api.Authenticator.Implicits.RichDateTime
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{ Clock, Credentials }
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.{ CredentialsProvider, SocialProviderRegistry }

import forms.LoginForm
import javax.inject.Inject
import models.services.api.UsuarioService
import net.ceedubs.ficus.Ficus.{ finiteDurationReader, optionValueReader, toFicusConfig }
import play.api.Configuration
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller
import utils.auth.DefaultEnv

class AutenticacaoController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  usuarioService: UsuarioService,
  authInfoRepository: AuthInfoRepository,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  clock: Clock,
  implicit val webJarAssets: WebJarAssets) extends Controller with I18nSupport {

  def login = silhouette.UnsecuredAction.async { implicit request =>
    LoginForm.form.bindFromRequest.fold(
      form => Future.successful(Redirect(routes.MainController.index())
        .flashing("error" -> Messages("login.form.invalido"))),
      data => {
        val credentials = Credentials(data.email, data.senha)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val result = Redirect(routes.MainController.index())
          usuarioService.retrieve(loginInfo).flatMap {
            case Some(usuario) if !usuario.ativado =>
              Future.successful(Ok(views.html.autenticacao.ativarConta(data.email)))
            case Some(usuario) =>
              val c = configuration.underlying
              silhouette.env.authenticatorService.create(loginInfo).map {
                case authenticator if data.lembrarMe =>
                  authenticator.copy(
                    expirationDateTime = clock.now + c.as[FiniteDuration](
                    "silhouette.authenticator.rememberMe.authenticatorExpiry"),
                    idleTimeout = c.getAs[FiniteDuration](
                      "silhouette.authenticator.rememberMe.authenticatorIdleTimeout"),
                    cookieMaxAge = c.getAs[FiniteDuration](
                      "silhouette.authenticator.rememberMe.rememberMe.cookieMaxAge"))
                case authenticator => authenticator
              }.flatMap { authenticator =>
                silhouette.env.eventBus.publish(LoginEvent(usuario, request))
                silhouette.env.authenticatorService.init(authenticator).flatMap { v =>
                  if (usuario.papel == "aluno") {
                    silhouette.env.authenticatorService.embed(v, Redirect(routes.AlunoController.respostas))
                  } else {
                    silhouette.env.authenticatorService.embed(v, Redirect(routes.ProfessorController.listas))
                  }
                }
              }
            case None => Future.failed(new IdentityNotFoundException("Não foi possível encontrar o usuário"))
          }
        }
      }.recover {
        case e: ProviderException =>
          Redirect(routes.MainController.index()).flashing("error" -> Messages("login.form.invalido"))
      })
  }

  def logout = silhouette.SecuredAction.async { implicit request =>
    silhouette.env.authenticatorService.discard(
      request.authenticator,
      Redirect(routes.MainController.index()))
  }
}
