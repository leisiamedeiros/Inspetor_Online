package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AlunoController extends Controller {
  def respostas = Action.async { implicit request =>
    /*
    AlunoDAO.list map { respostas =>
      //Ok(views.html.aluno.respostas(respostas))
    }
    */
    Future(Ok("ok"))
  }

  def listas = Action.async { implicit request =>
    /*
    ListaDAO.list map { listas =>
      //Ok(views.html.aluno.listas(listas))
      Ok(views.html.index)
    }
    */
    Future(Ok("ok"))
  }

  def questoes(id: Long) = Action.async { implicit request =>
    /*
    QuestaoDAO.list map { questoes =>
      //Ok(views.html.aluno.questoes(id, questoes))
    }
    */
    Future(Ok("ok"))
  }

  def novaResposta(lid: Long, qid: Long) = Action.async { implicit request =>
    /*
    QuestaoDAO.list map { respostas =>
      Ok(views.html.aluno.novaresposta(lid, AlunoForm.form, qid))
    }
    */
    Future(Ok("ok"))
  }

  def createResposta(lid: Long, qid: Long) = Action.async { implicit request =>
    /*
    AlunoForm.form.bindFromRequest.fold(
      errors => Future(BadRequest),
      data => {
        // Original (0, id, data.questao, data.resposta)
        val newQuestao = Aluno(0, lid, qid, data.resposta)
        AlunoDAO.add(newQuestao).map(res =>
          Redirect(routes.AlunoController.respostas())
        )
      })
    */
    Future(Ok("ok"))
  }
}
