package models

case class Questao(
  id: Int,
  numero: Int,
  enunciado: String,
  gabarito: String,
  idLista: Int
)
