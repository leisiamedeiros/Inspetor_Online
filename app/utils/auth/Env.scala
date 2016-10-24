package utils.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.Usuario

trait DefaultEnv extends Env {
  type I = Usuario
  type A = CookieAuthenticator
}
