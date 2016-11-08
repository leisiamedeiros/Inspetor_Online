package models.daos.impl

import models.{ Resposta, RespostaQuestao }
import models.daos.api.RespostaDAO

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

import java.util.UUID

class RespostaDAOImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider
) extends RespostaDAO {

  import driver.api._

  def add(instancia: Resposta): Future[Resposta] = {
    val bdResposta = BDResposta(
      instancia.id,
      instancia.linguagem,
      instancia.dados,
      instancia.estado,
      instancia.nota,
      instancia.usuarioID,
      instancia.questaoID
    )
    val query = respostas
    db.run(query += bdResposta).map(_ => instancia)
  }

  def list: Future[Seq[Resposta]] = {
    val query = respostas.result
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case (respostasRow) =>
          Resposta(respostasRow.id, respostasRow.linguagem, respostasRow.dados, respostasRow.estado,
            respostasRow.nota, respostasRow.usuarioID,
            respostasRow.questaoID)
      }
    }
    db.run(action)
  }

  def get(id: Int): Future[Option[Resposta]] = {
    val query = respostas
    db.run(query.filter(_.id === id).result.headOption).map {
      case (Some(respostasRow)) =>
        Some(Resposta(respostasRow.id, respostasRow.linguagem, respostasRow.dados, respostasRow.estado,
          respostasRow.nota, respostasRow.usuarioID,
          respostasRow.questaoID))
      case _ => None
    }
  }

  def listByAluno(id: UUID): Future[Seq[RespostaQuestao]] = {
    val query = listas
      .join(questoes).on(_.id === _.listaID)
      .join(respostas).on(_._2.id === _.questaoID)
      .filter(_._2.usuarioID === id).result
    val action = for {
      queryResult <- query
    } yield {
      queryResult.map {
        case ((listaRow, questaoRow), respostaRow) =>
          RespostaQuestao(
            listaRow.nome,
            questaoRow.numero,
            respostaRow.id,
            respostaRow.estado,
            respostaRow.nota
          )
      }
    }
    db.run(action)
  }

}
