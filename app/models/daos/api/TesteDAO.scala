package models.daos.api

import models.Teste

import scala.concurrent.Future

trait TesteDAO extends DAO {
  def add(instancia: Teste): Future[Teste]
  def list: Future[Seq[Teste]]
  def get(id: Int): Future[Option[Teste]]
  def listByQuestao(id: Int): Future[Seq[Teste]]
}
