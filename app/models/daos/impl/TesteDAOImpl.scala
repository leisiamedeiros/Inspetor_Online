package models.daos.impl

import concurrent.Future
import javax.inject.Inject
import models.Teste
import models.daos.api.{ TesteDAO }
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class TesteDAOImpl @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider) extends TesteDAO with DAO {

  import driver.api._

  override def add(instancia: Teste): Future[Teste] = {
    val bdTeste = BDTeste(
      instancia.id,
      instancia.entrada,
      instancia.saida,
      instancia.questaoID)
    val query = testes
    db.run(query += bdTeste).map(_ => instancia)
  }

  override def list: Future[Seq[Teste]] = {
    val query = testes.result
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case (testesRow) =>
          Teste(testesRow.id, testesRow.entrada, testesRow.saida, testesRow.questaoID)
      }
    }
    db.run(action)
  }

  override def get(id: Int): Future[Option[Teste]] = {
    val query = testes
    db.run(query.filter(_.id === id).result.headOption).map {
      case (Some(testesRow)) =>
        Some(Teste(testesRow.id, testesRow.entrada, testesRow.saida, testesRow.questaoID))
      case _ => None
    }
  }

  override def listByQuestao(id: Int): Future[Seq[Teste]] = {
    val query = testes.filter(_.questaoID === id).result
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case (testesRow) =>
          Teste(testesRow.id, testesRow.entrada, testesRow.saida, testesRow.questaoID)
      }
    }
    db.run(action)
  }

}
