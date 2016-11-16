package controllers

import concurrent.Await
import concurrent.ExecutionContext.Implicits.global
import concurrent.Future
import concurrent.duration.Duration
import util.control.Breaks.{ break, breakable }

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest

import forms.RespostaForm
import javax.inject.Inject
import models.{ Resposta, Usuario }
import models.daos.api.{ ListaDAO, QuestaoDAO, RespostaDAO, TesteDAO }
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.Controller
import utils.auth.{ DefaultEnv, WithRole }

class AlunoController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  listaDAO: ListaDAO,
  questaoDAO: QuestaoDAO,
  respostaDAO: RespostaDAO,
  testeDAO: TesteDAO) extends Controller with I18nSupport {

  val aluno = silhouette.SecuredAction(WithRole("aluno"))
  def usuario(implicit request: SecuredRequest[DefaultEnv, _]) = request.identity

  def respostas = aluno.async { implicit request =>
    respostaDAO.listByAluno(usuario.id).map { respostas =>
      Ok(views.html.aluno.respostas(respostas, usuario))
    }
  }

  def listas = aluno.async { implicit request =>
    listaDAO.list map { listas =>
      Ok(views.html.aluno.listas(listas, usuario))
    }
  }

  def questoes(id: Int) = aluno.async { implicit request =>
    questaoDAO.list map { questoes =>
      Ok(views.html.aluno.questoes(id, questoes, usuario))
    }
  }

  def resposta(id: Int) = aluno.async { implicit request =>
    respostaDAO.get(id) map { respostaOption =>
      respostaOption match {
        case Some(resposta) =>
          Ok(views.html.aluno.resposta(resposta, usuario))
        case None =>
          NotFound
      }
    }
  }

  def novaResposta(lid: Int, qid: Int) = aluno.async { implicit request =>
    questaoDAO.list map { respostas =>
      Ok(views.html.aluno.novaresposta(lid, RespostaForm.form, qid, usuario))
    }
  }

  def createResposta(lid: Int, qid: Int) = aluno.async { implicit request =>
    RespostaForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.aluno.novaresposta(lid, form, qid, usuario))),
      data => {
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
      })
  }
}
