package forms

import play.api.data._
import play.api.data.Forms._

case class AlterarPapelFormData(email: String, papel: String)

object AlterarPapelForm {
  val form = Form(
    mapping(
      "email" -> nonEmptyText,
      "papel" -> nonEmptyText
    )(AlterarPapelFormData.apply)(AlterarPapelFormData.unapply)
  )
}
