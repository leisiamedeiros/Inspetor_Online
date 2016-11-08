package models.daos.api

import models.Questao

import scala.concurrent.Future

trait QuestaoDAO extends DAO {
  def add(instancia: Questao): Future[Questao]
  def list: Future[Seq[Questao]]
  def get(id: Int): Future[Option[Questao]]
}
