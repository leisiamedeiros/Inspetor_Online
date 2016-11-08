package forms

import play.api.data._
import play.api.data.Forms._

case class AlunoFormData(questao: Long, resposta: String)

object AlunoForm {
  val form = Form(
    mapping(
      "questao" -> longNumber,
      "resposta" -> nonEmptyText
    )(AlunoFormData.apply)(AlunoFormData.unapply)
  )
}
