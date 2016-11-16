package controllers

import concurrent.Future

import com.mohiva.play.silhouette.api.Silhouette

import javax.inject.Inject
import models.Usuario
import models.daos.api.ListaDAO
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller
import utils.auth.DefaultEnv

class MainController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  listaDAO: ListaDAO) extends Controller with I18nSupport {
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
