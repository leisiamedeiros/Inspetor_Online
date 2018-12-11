package controllers

import java.util.UUID

import concurrent.Future

import com.mohiva.play.silhouette.api.{ LoginInfo, SignUpEvent, Silhouette }
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import forms.CadastroForm
import javax.inject.Inject
import models.Usuario
import models.services.api.{ AuthTokenService, UsuarioService }
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.mvc.Controller
import utils.auth.DefaultEnv

class CadastroController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  usuarioService: UsuarioService,
  authInfoRepository: AuthInfoRepository,
  authTokenService: AuthTokenService,
  avatarService: AvatarService,
  passwordHasherRegistry: PasswordHasherRegistry,
  mailerClient: MailerClient,
  implicit val webJarAssets: WebJarAssets) extends Controller with I18nSupport {
  def cadastro = silhouette.UnsecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.autenticacao.cadastro(CadastroForm.form)))
  }
  def enviar = silhouette.UnsecuredAction.async { implicit request =>
    CadastroForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.autenticacao.cadastro(form))),
      data => {
        val result = Ok(views.html.autenticacao.ativarConta(data.email))
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        usuarioService.retrieve(loginInfo).flatMap {
          case Some(usuario) =>
            val url = routes.MainController.index().absoluteURL()
            mailerClient.send(Email(
              subject = Messages("email.ja.cadastrado.assunto"),
              from = Messages("email.remetente"),
              to = Seq(data.email),
              bodyText = Some(views.txt.emails.jaCadastrado(usuario, url).body),
              bodyHtml = Some(views.html.emails.jaCadastrado(usuario, url).body)))
            Future.successful(result)
          case None =>
            val authInfo = passwordHasherRegistry.current.hash(data.senha)
            val usuario = Usuario(
              id = UUID.randomUUID(),
              loginInfo = loginInfo,
              papel = "aluno",
              nomeCompleto = data.nomeCompleto,
              email = data.email,
              avatarURL = None,
              ativado = false)
            for {
              avatar <- avatarService.retrieveURL(data.email)
              usuario <- usuarioService.save(usuario.copy(avatarURL = avatar))
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authToken <- authTokenService.create(usuario)
            } yield {
              val url = routes.AtivacaoContaController.ativar(authToken.id).absoluteURL()
              mailerClient.send(Email(
                subject = Messages("email.cadastrado.assunto"),
                from = Messages("email.remetente"),
                to = Seq(data.email),
                bodyText = Some(views.txt.emails.cadastro(usuario, url).body),
                bodyHtml = Some(views.html.emails.cadastro(usuario, url).body)))
              silhouette.env.eventBus.publish(SignUpEvent(usuario, request))
              result
            }
        }
      })
  }
}
