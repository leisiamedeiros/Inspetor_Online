package controllers

import com.mohiva.play.silhouette.api.Silhouette
import play.api._
import play.api.i18n.{ MessagesApi, I18nSupport }
import play.api.mvc._
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Await }
import scala.util.control.Breaks._

import javax.inject.Inject

import forms.RespostaForm
import models.{ Usuario, Resposta }
import models.daos.api.{ ListaDAO, RespostaDAO, QuestaoDAO, TesteDAO }
import utils.auth.{ DefaultEnv, WithRole }

class AlunoController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  listaDAO: ListaDAO,
  questaoDAO: QuestaoDAO,
  respostaDAO: RespostaDAO,
  testeDAO: TesteDAO
) extends Controller with I18nSupport {

  val aluno = silhouette.SecuredAction(WithRole(List("aluno", "admin")))

  def respostas = aluno.async { implicit request =>
    val usuario: Usuario = request.identity
    respostaDAO.listByAluno(usuario.id).map { respostas =>
      Ok(views.html.aluno.respostas(respostas, request.identity))
    }
  }

  def listas = aluno.async { implicit request =>
    listaDAO.list map { listas =>
      Ok(views.html.aluno.listas(listas, request.identity))
    }
  }

  def questoes(id: Int) = aluno.async { implicit request =>
    questaoDAO.list map { questoes =>
      Ok(views.html.aluno.questoes(id, questoes, request.identity))
    }
  }

  def resposta(id: Int) = aluno.async { implicit request =>
    respostaDAO.get(id) map { respostaOption =>
      respostaOption match {
        case Some(resposta) =>
          Ok(views.html.aluno.resposta(resposta, request.identity))
        case None =>
          NotFound
      }
    }
  }

  def novaResposta(lid: Int, qid: Int) = aluno.async { implicit request =>
    questaoDAO.list map { respostas =>
      Ok(views.html.aluno.novaresposta(lid, RespostaForm.form, qid, request.identity))
    }
  }

  def createResposta(lid: Int, qid: Int) = aluno.async { implicit request =>
    RespostaForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.aluno.novaresposta(lid, form, qid, request.identity))),
      data => {
        val usuario: Usuario = request.identity
        val resposta = Resposta(0, data.linguagem, data.dados, "n", None, usuario.id, qid)

        // RODANDO TESTES
        testeDAO.listByQuestao(qid).map { testes =>
          breakable {
            for (teste <- testes) {
              if (!(resposta == teste)) {
                break
              }
            }
          }
          Await.result(respostaDAO.add(resposta).map { created =>
            Redirect(routes.AlunoController.respostas)
          }, Duration.Inf)
        }
      }
    )
  }
}
