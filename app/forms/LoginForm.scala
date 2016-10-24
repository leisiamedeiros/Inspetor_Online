package forms

import play.api.data.Form
import play.api.data.Forms._

object LoginForm {
  val form = Form(
    mapping(
      "email" -> nonEmptyText,
      "senha" -> nonEmptyText,
      "lembrarMe" -> boolean
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    email: String,
    senha: String,
    lembrarMe: Boolean
  )
}
