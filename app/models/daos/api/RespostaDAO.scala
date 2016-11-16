package models.daos.api

import java.util.UUID

import concurrent.Future

import models.{ Resposta, RespostaQuestao }

trait RespostaDAO extends DAO {
  def add(instancia: Resposta): Future[Resposta]
  def list: Future[Seq[Resposta]]
  def listByAluno(id: UUID): Future[Seq[RespostaQuestao]]
  def get(id: Int): Future[Option[Resposta]]
}
