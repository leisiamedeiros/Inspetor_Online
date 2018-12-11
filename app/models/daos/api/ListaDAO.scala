package models.daos.api

import java.util.UUID

import concurrent.Future

import models.Lista

trait ListaDAO {
  def add(instancia: Lista): Future[Lista]
  def list: Future[Seq[Lista]]
  def get(id: Int): Future[Option[Lista]]
  def getByProfessor(id: UUID): Future[Seq[Lista]]
}
