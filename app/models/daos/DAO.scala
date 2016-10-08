package models.daos

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

trait DAO[T] {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  def add(instancia: T): Future[String]
  def list: Future[Seq[T]]
  def get(id: Long): Future[Option[T]]
}

