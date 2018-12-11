package forms

import play.api.data.Form
import play.api.data.Forms.{ longNumber, mapping, nonEmptyText }

case class AlunoFormData(questao: Long, resposta: String)

object AlunoForm {
  val form = Form(
    mapping(
      "questao" -> longNumber,
      "resposta" -> nonEmptyText)(AlunoFormData.apply)(AlunoFormData.unapply))
}
