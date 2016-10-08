package models.daos

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.PostgresDriver.api._
import models.{Lista, ListaTable}

object ListaDAO extends DAO[Lista] {
  override def add(instancia: Lista): Future[String] = {
    val query = TableQuery[ListaTable]
    dbConfig.db.run(query += instancia).map(res => "Lista successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }
  override def list: Future[Seq[Lista]] = {
    val query = TableQuery[ListaTable]
    dbConfig.db.run(query.result)
  }
  override def get(id: Long): Future[Option[Lista]] = {
    val query = TableQuery[ListaTable]
    dbConfig.db.run(query.filter(_.id === id).result.headOption)
  }
}
