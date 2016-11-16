package utils.auth

import concurrent.Future

import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler

import play.api.mvc.RequestHeader
import play.api.mvc.Results.Redirect

class CustomUnsecuredErrorHandler extends UnsecuredErrorHandler {
  override def onNotAuthorized(implicit request: RequestHeader) = {
    Future.successful(Redirect(controllers.routes.MainController.index()))
  }
}
