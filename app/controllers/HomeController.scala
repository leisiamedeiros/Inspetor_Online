package controllers


import model._
import play.api._
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.i18n.{MessagesApi, I18nSupport}

class HomeController extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def aluno = Action.async { implicit request =>
    ListasQuestoes.listquestionaluno map { respostas =>
      Ok(views.html.aluno(respostas))
    }
  }

  def novarespostaquestao = Action.async { implicit request =>
    ListasQuestoes.listAll map { listas =>
      Ok(views.html.questaoaluno(listas))
    }
  }

  def questoeslista(id: Long) = Action.async { implicit request =>
    ListasQuestoes.listquestion map { questoes =>
      Ok(views.html.questoeslista(id, questoes))
      }
    }

  def alunoresposta(id: Long, a: Long) = Action.async { implicit request =>
    ListasQuestoes.listquestion map { respostas =>
      Ok(views.html.novaquestaoaluno(id, AlunoForm.form, a))
    }
  }

  def createquestaoaluno(id: Long) = Action.async { implicit request =>
    AlunoForm.form.bindFromRequest.fold(
      errors => Future(BadRequest),
      data => {
        val newQuestao = Aluno(0, id, data.questao, data.resposta)
        ListasQuestoes.addresposta(newQuestao).map(res =>
          Redirect(routes.HomeController.aluno())
        )
      })
  }

  def professor = Action.async { implicit request =>
    ListasQuestoes.listAll map { listas =>
      Ok(views.html.professor(listas))
    }
  }

  def novalista = Action.async { implicit request =>
    ListasQuestoes.listAll map { listas =>
      Ok(views.html.novalista(ListaForm.form))
    }
  }

  def questoes(id: Long) = Action.async { implicit request =>
    ListasQuestoes.listquestion map { questoes =>
      Ok(views.html.questoes(id, questoes))
    }
  }

  def novaquestao(id: Long) = Action.async { implicit request =>
    ListasQuestoes.listquestion map { questoes =>
      Ok(views.html.novaquestao(id, QuestaoForm.form))
    }
  }

  def createlista() = Action.async { implicit request =>
    ListaForm.form.bindFromRequest.fold(
      errors => Future(BadRequest),
      data => {
        val newLista = Lista(0, data.nome, data.assunto)
        ListasQuestoes.addlista(newLista).map(res =>
          Redirect(routes.HomeController.professor())
        )
      })
  }

  def createquestao(id: Long) = Action.async { implicit request =>
    QuestaoForm.form.bindFromRequest.fold(
      errors => Future(BadRequest),
      data => {
        val newQuestao = Questao(0, data.numero, data.enunciado, data.gabarito, data.entrada, data.saida, id)
        ListasQuestoes.addquestao(newQuestao).map(res =>
          Redirect(routes.HomeController.questoes(id))
        )
      })
  }


}
