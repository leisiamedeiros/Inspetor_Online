package models

import slick.driver.MySQLDriver.api._

case class Lista(id: Long, nome: String, assunto: String)

class ListaTable(tag: Tag) extends Table[Lista](tag, "lista") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def nome = column[String]("nome")
  def assunto = column[String]("assunto")
  override def * = (id,nome,assunto) <>(Lista.tupled, Lista.unapply)
}
