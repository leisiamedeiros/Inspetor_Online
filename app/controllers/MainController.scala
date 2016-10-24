package controllers

import play.api._
import play.api.mvc._
import play.api.i18n.{ MessagesApi, I18nSupport }
import play.api.libs.concurrent.Execution.Implicits._

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.duration._
import com.mohiva.play.silhouette.api.LoginInfo
import models.{ Usuario, AuthToken }
import models.services.api.{ AuthTokenService, UsuarioService }

import scala.concurrent.{ Future, Await }

class MainController @Inject() (authTokenService: AuthTokenService, usuarioService: UsuarioService) extends Controller {
  def index = Action.async {
    //usuarioDAO.save(new Usuario(
    //  UUID.randomUUID(),
    //  new LoginInfo("b", "b"),
    //  "professor",
    //  "Professor de Teste",
    //  "professor@teste.com",
    //  None
    //))
    //usuarioDAO.find(UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d")).flatMap {
    //usuarioDAO.find(new LoginInfo("a", "a")).flatMap {
    //  case Some(usuario) =>
    //    Future.successful(Redirect(routes.HomeController.index()).flashing("usuario" -> usuario.toString()))
    //  case None =>
    //    Future.successful(Redirect(routes.HomeController.index()).flashing("usuario" -> "nao_tem"))
    //}
    //val uuid = UUID.randomUUID()
    //usuarioService.save(new Usuario(
    //uuid,
    //new LoginInfo(UUID.randomUUID().toString, UUID.randomUUID().toString),
    //"aluno",
    //"Aluno de Teste",
    //"aluno@teste.com",
    //None
    //)).flatMap {
    //case usuario: Usuario =>
    //authTokenService.create(usuario, 30 seconds).flatMap {
    //case token: AuthToken =>
    //Future.successful(Ok("Ok"))
    //case _ => Future.successful(Ok("Was not possible to save the token"))
    //}
    //case _ => Future.successful(Ok("Was not possible to save the user"))
    //}
    //val tokens = authTokenDAO.findExpired(DateTime.now)
    //tokens.map { tokensSeq =>
    //val str = tokensSeq.map(_.id.toString()).mkString(", ")
    //Ok(str)
    //}
    //authTokenService.clean.flatMap {
    //case _ => Future.successful(Ok("Ok"))
    //}
    Future.successful(Ok("Ok"))
  }
}
