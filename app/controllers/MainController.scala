package controllers

import com.mohiva.play.silhouette.api._
import play.api._
import play.api.mvc._
import play.api.i18n.{ MessagesApi, I18nSupport }
import play.api.libs.concurrent.Execution.Implicits._
import play.filters.csrf._

import java.util.UUID
import javax.inject.Inject

import models.Usuario
import models.daos.api.ListaDAO
import utils.auth.DefaultEnv

import scala.concurrent.Future

class MainController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  listaDAO: ListaDAO
) extends Controller with I18nSupport {
  def index = silhouette.UserAwareAction.async { implicit request =>
    val usuario: Option[Usuario] = request.identity
    usuario match {
      case Some(u) => {
        listaDAO.getByProfessor(u.id) map { listas =>
          Ok(views.html.index(Some(u), Some(listas)))
        }
      }
      case None => Future.successful(Ok(views.html.index(None, None)))
    }
  }
}
