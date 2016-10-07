package models

import slick.driver.MySQLDriver.api._

case class Aluno(id: Long, lista: Long, questao: Long, resposta: String)

class AlunoTable(tag: Tag) extends Table[Aluno](tag, "aluno") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def lista = column[Long]("lista")
  def questao = column[Long]("questao")
  def resposta = column[String]("resposta")
  override def * = (id,lista,questao,resposta) <>(Aluno.tupled, Aluno.unapply)

}
