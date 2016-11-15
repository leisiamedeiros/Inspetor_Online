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
  private def toHtml(s: String) = s.replace("\n", "</br>").replace("\r", "&nbsp;&nbsp;")
  def gabaritoToHtml: String = toHtml(gabarito)
  def entradaToHtml: String = toHtml(entrada)
  def saidaToHtml: String = toHtml(saida)
}
