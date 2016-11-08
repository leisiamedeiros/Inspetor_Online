package models.daos.api

import models.{ Resposta, RespostaQuestao }

import java.util.UUID

import scala.concurrent.Future

trait RespostaDAO extends DAO {
  def add(instancia: Resposta): Future[Resposta]
  def list: Future[Seq[Resposta]]
  def listByAluno(id: UUID): Future[Seq[RespostaQuestao]]
  def get(id: Int): Future[Option[Resposta]]
}
