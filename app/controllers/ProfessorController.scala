package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProfessorController extends Controller {
  def listas = Action.async { implicit request =>
    /*
    ListaDAO.list map { listas =>
      Ok(views.html.professor.listas(listas))
    }
    */
    Future(Ok("ok"))
  }

  def novaLista = Action.async { implicit request =>
    // ListaDAO.list map { listas =>
    //   Ok(views.html.professor.novalista(ListaForm.form))
    // }
    Future(Ok("ok"))
  }

  def createLista = Action.async { implicit request =>
    // ListaForm.form.bindFromRequest.fold(
    //   errors => Future(BadRequest),
    //   data => {
    //     val newLista = Lista(0, data.nome, data.assunto)
    //     ListaDAO.add(newLista).map(res =>
    //       Redirect(routes.ProfessorController.listas())
    //     )
    //   })
    Future(Ok("ok"))
  }

  def questoes(id: Long) = Action.async { implicit request =>
    //QuestaoDAO.list map { questoes =>
    //  Ok(views.html.professor.questoes(id, questoes))
    // }
    Future(Ok("ok"))
  }

  def novaQuestao(id: Long) = Action.async { implicit request =>
    //QuestaoDAO.list map { questoes =>
    //  Ok(views.html.professor.novaquestao(id, QuestaoForm.form))
    // }
    Future(Ok("ok"))
  }

  def createQuestao(id: Long) = Action.async { implicit request =>
    /*
    QuestaoForm.form.bindFromRequest.fold(
      errors => Future(BadRequest),
      data => {
        val newQuestao = Questao(0, data.numero, data.enunciado, data.gabarito, data.entrada, data.saida, id)
        QuestaoDAO.add(newQuestao).map(res =>
          Redirect(routes.ProfessorController.questoes(id))
        )
      })
    */
    Future(Ok("ok"))
  }

}
