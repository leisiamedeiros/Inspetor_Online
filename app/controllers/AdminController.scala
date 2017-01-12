package controllers

import com.mohiva.play.silhouette.api.Silhouette
import play.api._
import play.api.i18n.{ MessagesApi, I18nSupport }
import play.api.mvc._
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Await }
import scala.util.control.Breaks._

import javax.inject.Inject

import forms.AlterarPapelForm
import models.{ Usuario, Resposta }
import models.services.api.UsuarioService
import utils.auth.{ DefaultEnv, WithRole }

class AdminController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  usuarioService: UsuarioService
) extends Controller with I18nSupport {

  val admin = silhouette.SecuredAction(WithRole(List("admin")))

  //def pesquisarUsuario = admin.async { implicit request =>
  //}

  def alterarPapel = admin.async { implicit request =>
    Future.successful(Ok(views.html.admin.alterarPapel(AlterarPapelForm.form, request.identity)))
  }

  def fazerAlteracao = admin.async { implicit request =>
    AlterarPapelForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.admin.alterarPapel(form, request.identity))),
      data => {
        usuarioService.retrieve(data.email).map { result =>
          result.map { usuario =>
            usuarioService.save(usuario.copy(papel = data.papel))
          }
          Redirect(routes.AdminController.alterarPapel)
        }
      }
    )
  }
}
