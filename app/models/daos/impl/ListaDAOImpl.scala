package models.daos.impl

import models.Lista
import models.daos.api.ListaDAO

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class ListaDAOImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) extends ListaDAO {

  import driver.api._

  // def add(instancia: Lista): Future[String] = {
  //   val query = listas
  //   dbConfig.db.run(query += instancia).map(res => "Lista successfully added").recover {
  //     case ex: Exception => ex.getCause.getMessage
  //   }
  // }

  def list: Future[Seq[Lista]] = {
    //dbConfig.db.run(listas.result).map(Some(_))
    //  val q = for (c <- listas) yield c
    //val a = q.result
    //dbConfig.db.run(listas.result).map(rows => Some(_))
    //rows.map { case (q) => (q) })
    val query = listas.result
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case (listasRow) =>
          Lista(listasRow.id, listasRow.nome, listasRow.assunto)
      }
    }
    db.run(action)
  }

  // def findExpired(dateTime: DateTime): Future[Seq[AuthToken]] = {
  //   val query = authTokens.filter(_.expiry <= dateTime).result
  //   val action = for {
  //     queryResult <- query
  //   } yield {
  //     queryResult.map {
  //       case (authTokenRow) =>
  //         AuthToken(
  //           authTokenRow.id,
  //           authTokenRow.expiry,
  //           authTokenRow.usuarioID
  //         )
  //     }
  //   }
  //   db.run(action)
  // }

  // def get(id: Long): Future[Option[Lista]] = {
  //   val query = listas
  //   dbConfig.db.run(query.filter(_.id === id).result.headOption)
  // }

}
