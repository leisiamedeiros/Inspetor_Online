package models

import java.util.UUID

import interpretadores.InterpretadorFabrica

case class Resposta(
  id: Int,
  linguagem: String,
  dados: String,
  var estado: String,
  var nota: Option[Float],
  usuarioID: UUID,
  questaoID: Int) {

  def compare(teste: Teste): Boolean = {
    val s = InterpretadorFabrica(this.linguagem)
      .interpretar(this.dados, teste.entrada)

    s match {
      case Some(saida) => {
        // RESPOSTA CORRETA
        if (saida.equals(teste.saida)) {
          this.nota = Some(10)
          this.estado = "OK"
          return true
        }

        this.nota = Some(0)

        // RESPOSTA INCORRETA (Case Sensitive)
        if (saida.equalsIgnoreCase(teste.saida)) {
          this.estado = "C"
          return false
        }

        // RESPOSTA INCORRETA (Espaços a mais)
        val saida_ws = saida.replaceAll("\\s+", "")
        val teste_saida_ws = teste.saida.replaceAll("\\s+", "")

        if (saida_ws.equals(teste_saida_ws)) {
          this.estado = "S"
          return false
        }

        // RESPOSTA INCORRETA (Case Sensitive e Espaços a mais)
        if (saida_ws.equalsIgnoreCase(teste_saida_ws)) {
          this.estado = "CS"
          return false
        }
      }
      case None =>
    }

    // RESPOSTA INCORRETA
    this.estado = "E"
    return false

  }

  override def equals(that: Any): Boolean =
    that match {
      case that: Teste => this.compare(that)
      case _ => false
    }
}

case class RespostaQuestao(
  listaNome: String,
  questaoNumero: Int,
  respostaID: Int,
  respostaEstado: String,
  respostaNota: Option[Float])
