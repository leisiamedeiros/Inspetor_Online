package models

import java.util.UUID

case class Lista(
  id: Int,
  nome: String,
  assunto: String,
  usuarioID: UUID
)
