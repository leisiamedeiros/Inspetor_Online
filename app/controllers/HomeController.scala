package controllers

import play.api._
import play.api.mvc._
import play.api.i18n.{MessagesApi, I18nSupport}

class HomeController extends Controller {
  def index = Action {
    Ok(views.html.index())
  }
}
