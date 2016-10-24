package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

case class Usuario(
  id: UUID,
  loginInfo: LoginInfo,
  papel: String,
  nomeCompleto: String,
  email: String,
  avatarURL: Option[String],
  ativado: Boolean
) extends Identity
