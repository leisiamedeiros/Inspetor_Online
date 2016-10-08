package models.daos

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.PostgresDriver.api._
import models.{Aluno, AlunoTable}

object AlunoDAO extends DAO[Aluno] {
  override def add(instancia: Aluno): Future[String] = {
    val query = TableQuery[AlunoTable]
    dbConfig.db.run(query += instancia).map(res => "Aluno successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }
  override def list: Future[Seq[Aluno]] = {
    val query = TableQuery[AlunoTable]
    dbConfig.db.run(query.result)
  }
  override def get(id: Long): Future[Option[Aluno]] = {
    val query = TableQuery[AlunoTable]
    dbConfig.db.run(query.filter(_.id === id).result.headOption)
  }
}
