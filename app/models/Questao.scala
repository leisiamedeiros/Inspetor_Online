package models

import slick.driver.MySQLDriver.api._

case class Questao(id: Long, numero: Long, enunciado: String, gabarito: String, entrada: String, saida: String, idLista: Long)

class QuestaoTable(tag: Tag) extends Table[Questao](tag, "questao") {
  val lista = TableQuery[ListaTable]
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def numero = column[Long]("numero")
  def enunciado = column[String]("enunciado")
  def gabarito = column[String]("gabarito")
  def entrada = column[String]("entrada")
  def saida = column[String]("saida")
  def idLista = column[Long]("idLista")
  override def * = (id,numero,enunciado,gabarito,entrada,saida,idLista) <>(Questao.tupled, Questao.unapply)

  def questaolista = foreignKey("fk_questao_lista",idLista, lista)(_.id)
}
