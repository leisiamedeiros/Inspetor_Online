package models.daos.api

import concurrent.Future

import models.Teste

trait TesteDAO {
  def add(instancia: Teste): Future[Teste]
  def list: Future[Seq[Teste]]
  def get(id: Int): Future[Option[Teste]]
  def listByQuestao(id: Int): Future[Seq[Teste]]
}
