package models

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

case class Usuario(
  id: Int,
  loginInfo: LoginInfo,
  nomeCompleto: String,
  email: String,
  avatarURL: Option[String]
) extends Identity
