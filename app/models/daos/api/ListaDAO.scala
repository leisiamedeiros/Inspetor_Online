package models.daos.api

import models.Lista

import scala.concurrent.Future

import java.util.UUID

trait ListaDAO extends DAO {
  def add(instancia: Lista): Future[Lista]
  def list: Future[Seq[Lista]]
  def get(id: Int): Future[Option[Lista]]
  def getByProfessor(id: UUID): Future[Seq[Lista]]
}
