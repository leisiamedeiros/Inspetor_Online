package utils.auth

import concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler

import javax.inject.Inject
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc.RequestHeader
import play.api.mvc.Results.Redirect

class CustomSecuredErrorHandler @Inject() (
  val messagesApi: MessagesApi) extends SecuredErrorHandler with I18nSupport {
  override def onNotAuthenticated(implicit request: RequestHeader) = {
    Future.successful(Redirect(controllers.routes.MainController.index()))
  }
  override def onNotAuthorized(implicit request: RequestHeader) = {
    Future.successful(
      Redirect(controllers.routes.MainController.index())
        .flashing("error" -> Messages("acesso.negado")))
  }
}
