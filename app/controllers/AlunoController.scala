package controllers

import forms.AlunoForm
import play.api._
import play.api.mvc._
import models.Aluno
import models.daos.{AlunoDAO, ListaDAO, QuestaoDAO}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AlunoController extends Controller {
  def respostas = Action.async { implicit request =>
    AlunoDAO.list map { respostas =>
      Ok(views.html.aluno.respostas(respostas))
    }
  }

  def listas = Action.async { implicit request =>
    ListaDAO.list map { listas =>
      Ok(views.html.aluno.listas(listas))
    }
  }

  def questoes(id: Long) = Action.async { implicit request =>
    QuestaoDAO.list map { questoes =>
      Ok(views.html.aluno.questoes(id, questoes))
      }
    }

  def novaResposta(lid: Long, qid: Long) = Action.async { implicit request =>
    QuestaoDAO.list map { respostas =>
      Ok(views.html.aluno.novaresposta(lid, AlunoForm.form, qid))
    }
  }

  def createResposta(lid: Long, qid: Long) = Action.async { implicit request =>
    AlunoForm.form.bindFromRequest.fold(
      errors => Future(BadRequest),
      data => {
        // Original (0, id, data.questao, data.resposta)
        val newQuestao = Aluno(0, lid, qid, data.resposta)
        AlunoDAO.add(newQuestao).map(res =>
          Redirect(routes.AlunoController.respostas())
        )
      })
  }
}
