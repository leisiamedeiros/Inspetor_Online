package forms

import play.api.data._
import play.api.data.Forms._

case class RespostaFormData(dados: String, linguagem: String)

object RespostaForm {
  val form = Form(
    mapping(
      "dados" -> nonEmptyText,
      "linguagem" -> nonEmptyText
    )(RespostaFormData.apply)(RespostaFormData.unapply)
  )
}
