package daos

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DAO[T] {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  def query: TableQuery[Table[T]]
  def add(instancia: T): Future[String]
  def list(instancia: T): Future[Seq[T]]
  def get(instancia: T): Future[Option[T]]
}

