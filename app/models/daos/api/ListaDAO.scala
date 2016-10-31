package models.daos.api

import models.Lista

import scala.concurrent.Future

trait ListaDAO extends DAO {
  def add(instancia: Lista): Future[String]
  def list: Future[Seq[Lista]]
  def get(id: Long): Future[Option[Lista]]
}
