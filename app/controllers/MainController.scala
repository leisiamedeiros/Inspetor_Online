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
    Future.successful(Ok(views.html.index()))
  }
}
