package forms

import play.api.data._
import play.api.data.Forms._

case class ListaFormData(nome: String, assunto: String)

object ListaForm {
  val form = Form(
    mapping(
      "nome" -> nonEmptyText,
      "assunto" -> nonEmptyText
    )(ListaFormData.apply)(ListaFormData.unapply)
  )
}
