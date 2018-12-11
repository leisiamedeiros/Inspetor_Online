package forms

import play.api.data.Form
import play.api.data.Forms.{ mapping, nonEmptyText }

case class ListaFormData(nome: String, assunto: String)

object ListaForm {
  val form = Form(
    mapping(
      "nome" -> nonEmptyText,
      "assunto" -> nonEmptyText)(ListaFormData.apply)(ListaFormData.unapply))
}
