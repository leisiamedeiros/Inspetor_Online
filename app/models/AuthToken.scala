package models

import java.util.UUID

import org.joda.time.DateTime

case class AuthToken(
  id: UUID,
  expiry: DateTime,
  usuarioID: UUID)
