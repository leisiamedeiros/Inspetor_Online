package models.services.api

import java.util.UUID

import concurrent.Future

import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile

import models.Usuario

trait UsuarioService extends IdentityService[Usuario] {
  def retrieve(id: UUID): Future[Option[Usuario]]
  def retrieve(email: String): Future[Option[Usuario]]
  def save(usuario: Usuario): Future[Usuario]
  def save(profile: CommonSocialProfile): Future[Usuario]
}
