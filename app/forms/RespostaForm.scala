package forms

import play.api.data.Form
import play.api.data.Forms.{ mapping, nonEmptyText }

case class RespostaFormData(dados: String, linguagem: String)

object RespostaForm {
  val form = Form(
    mapping(
      "dados" -> nonEmptyText,
      "linguagem" -> nonEmptyText)(RespostaFormData.apply)(RespostaFormData.unapply))
}
