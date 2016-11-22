package models.daos.impl

import concurrent.Future
import javax.inject.Inject
import models.Questao
import models.daos.api.{ QuestaoDAO }
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class QuestaoDAOImpl @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider) extends QuestaoDAO with DAO {

  import driver.api._

  def add(instancia: Questao): Future[Questao] = {
    val bdQuestao = BDQuestao(
      instancia.id,
      instancia.numero,
      instancia.enunciado,
      instancia.entrada,
      instancia.saida,
      instancia.gabarito,
      instancia.listaID)
    val query = questoes returning questoes.map(_.id) into ((questao, id) => questao.copy(id = id))
    db.run(query += bdQuestao).map(q => Questao(q.id, q.numero, q.enunciado, q.entrada, q.saida, q.gabarito, q.listaID))
  }

  def list: Future[Seq[Questao]] = {
    val query = questoes.result
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case (questoesRow) =>
          Questao(questoesRow.id, questoesRow.numero, questoesRow.enunciado,
            questoesRow.entrada, questoesRow.saida, questoesRow.gabarito, questoesRow.listaID)
      }
    }
    db.run(action)
  }

  def get(id: Int): Future[Option[Questao]] = {
    val query = questoes
    db.run(query.filter(_.id === id).result.headOption).map {
      case (Some(questoesRow)) =>
        Some(Questao(questoesRow.id, questoesRow.numero, questoesRow.enunciado,
          questoesRow.entrada, questoesRow.saida, questoesRow.gabarito, questoesRow.listaID))
      case _ => None
    }
  }

}
