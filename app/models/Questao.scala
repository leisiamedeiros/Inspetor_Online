package models

case class Questao(
  id: Int,
  numero: Int,
  enunciado: String,
  entrada: String,
  saida: String,
  gabarito: String,
  listaID: Int
) {
  def gabaritoToHtml: String = {
    gabarito.replace("\n", "</br>").replace("\r", "&nbsp;&nbsp;")
  }
  def entradaToHtml: String = {
    entrada.replace("\n", "</br>").replace("\r", "&nbsp;&nbsp;")
  }
  def saidaToHtml: String = {
    saida.replace("\n", "</br>").replace("\r", "&nbsp;&nbsp;")
  }
}
