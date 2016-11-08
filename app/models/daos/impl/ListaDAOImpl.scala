package models.daos.impl

import models.Lista
import models.daos.api.ListaDAO

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

import java.util.UUID

class ListaDAOImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) extends ListaDAO {

  import driver.api._

  def add(instancia: Lista): Future[Lista] = {
    val bdLista = BDLista(
      instancia.id,
      instancia.nome,
      instancia.assunto,
      instancia.usuarioID
    )
    val query = listas
    db.run(query += bdLista).map(_ => instancia)
  }

  def list: Future[Seq[Lista]] = {
    val query = listas.result
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case (listasRow) =>
          Lista(listasRow.id, listasRow.nome, listasRow.assunto, listasRow.usuarioID)
      }
    }
    db.run(action)
  }

  def getByProfessor(id: UUID): Future[Seq[Lista]] = {
    val query = listas
    db.run(query.filter(_.usuarioID === id).result).map { listas =>
      listas.map {
        case (listasRow) =>
          Lista(listasRow.id, listasRow.nome, listasRow.assunto, listasRow.usuarioID)
      }
    }
  }

  def get(id: Int): Future[Option[Lista]] = {
    val query = listas
    db.run(query.filter(_.id === id).result.headOption).map {
      case (Some(listasRow)) =>
        Some(Lista(listasRow.id, listasRow.nome, listasRow.assunto, listasRow.usuarioID))
      case _ => None
    }
  }

}
