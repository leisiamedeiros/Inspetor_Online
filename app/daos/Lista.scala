package daos

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Lista extends DAO[Lista] {
  override def query: TableQuery[Table[Lista]] = {
    return TableQuery[ListaTable]
  }
  override def add(instancia: Lista): Future[String] = {
    dbConfig.db.run(query += instancia).map(res => "Lista successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }
  override def list(instancia: Lista): Future[Seq[Lista]] = {
    dbConfig.db.run(query.result)
  }
  override def get(instancia: Lista): Future[Option[Lista]] = {
    dbConfig.db.run(query.filter(_.id === id).result.headOption)
  }
}
