package models.daos

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.PostgresDriver.api._
import models.{Questao, QuestaoTable}

object QuestaoDAO extends DAO[Questao] {
  override def add(instancia: Questao): Future[String] = {
    val query = TableQuery[QuestaoTable]
    dbConfig.db.run(query += instancia).map(res => "Questao successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }
  override def list: Future[Seq[Questao]] = {
    val query = TableQuery[QuestaoTable]
    dbConfig.db.run(query.result)
  }
  override def get(id: Long): Future[Option[Questao]] = {
    val query = TableQuery[QuestaoTable]
    dbConfig.db.run(query.filter(_.id === id).result.headOption)
  }
}
