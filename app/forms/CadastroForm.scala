package forms

import play.api.data.Form
import play.api.data.Forms.{ email, mapping, nonEmptyText }

object CadastroForm {
  val form = Form(
    mapping(
      "nomeCompleto" -> nonEmptyText,
      "email" -> email,
      "senha" -> nonEmptyText)(Data.apply)(Data.unapply))

  case class Data(
    nomeCompleto: String,
    email: String,
    senha: String)
}
