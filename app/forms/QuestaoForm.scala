package forms

import play.api.data._
import play.api.data.Forms._

case class QuestaoFormData(numero: Int, enunciado: String, gabarito: String, entrada: String, saida: String)

object QuestaoForm {
  val form = Form(
    mapping(
      "numero" -> number,
      "enunciado" -> nonEmptyText,
      "gabarito" -> nonEmptyText,
      "entrada" -> text,
      "saida" -> nonEmptyText
    )(QuestaoFormData.apply)(QuestaoFormData.unapply)
  )
}
