package forms

import play.api.data._
import play.api.data.Forms._

case class QuestaoFormData(numero: Long, enunciado: String, gabarito: String, entrada: String, saida: String)

object QuestaoForm {
  val form = Form(
    mapping(
      "numero" -> longNumber,
      "enunciado" -> nonEmptyText,
      "gabarito" -> nonEmptyText,
      "entrada" -> nonEmptyText,
      "saida" -> nonEmptyText
    )(QuestaoFormData.apply)(QuestaoFormData.unapply)
  )
}
