package controllers

import java.io.File

import concurrent.Await
import concurrent.ExecutionContext.Implicits.global
import concurrent.Future
import concurrent.duration.Duration
import scala.io.Source

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest

import forms.{ ListaForm, QuestaoForm }
import javax.inject.Inject
import models.{ Lista, Questao, Teste }
import models.daos.api.{ ListaDAO, QuestaoDAO, TesteDAO }
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.libs.Files.TemporaryFile
import play.api.mvc.Controller
import play.api.mvc.MultipartFormData.FilePart
import utils.auth.{ DefaultEnv, WithRole }

class ProfessorController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  listaDAO: ListaDAO,
  questaoDAO: QuestaoDAO,
  testeDAO: TesteDAO) extends Controller with I18nSupport {

  val professor = silhouette.SecuredAction(WithRole("professor"))
  def usuario(implicit request: SecuredRequest[DefaultEnv, _]) = request.identity

  def listas = professor.async { implicit request =>
    listaDAO.getByProfessor(usuario.id) map { listas =>
      Ok(views.html.professor.listas(listas, usuario))
    }
  }

  def novaLista = professor.async { implicit request =>
    listaDAO.getByProfessor(usuario.id) map { listas =>
      Ok(views.html.professor.novalista(ListaForm.form, usuario, listas))
    }
  }

  def createLista = professor.async { implicit request =>
    ListaForm.form.bindFromRequest.fold(
      errors => Future(BadRequest),
      data => {
        val newLista = Lista(0, data.nome, data.assunto, usuario.id)
        listaDAO.add(newLista).map(res =>
          Redirect(routes.ProfessorController.listas()))
      })
  }

  def questoes(id: Int) = professor.async { implicit request =>
    listaDAO.getByProfessor(usuario.id) map { listas =>
      Await.result(questaoDAO.list map { q =>
        Ok(views.html.professor.questoes(id, q, usuario, listas))
      }, Duration.Inf)
    }
  }

  def novaQuestao(id: Int) = professor.async { implicit request =>
    listaDAO.getByProfessor(usuario.id) map { listas =>
      Ok(views.html.professor.novaquestao(id, QuestaoForm.form, usuario, listas))
    }
  }

  def interpretarTeste(teste: File): (String, String) = {
    val linhas = Source.fromFile(teste).getLines
    val (entrada, saida, _) = linhas.foldLeft("", "", true) {
      case (a @ (entrada, saida, a3), b) => b match {
        case "#entrada" => (entrada, saida, true)
        case "#saida" => (entrada, saida, false)
        case texto if a3 => (entrada + texto + "\n", saida, true)
        case texto => (entrada, saida + texto + "\n", false)
      }
    }
    return (entrada, saida)
  }

  def createQuestao(id: Int) = professor.async { implicit request =>
    QuestaoForm.form.bindFromRequest.fold(
      errors => Future(BadRequest),
      data => {
        val newQuestao = Questao(0, data.numero, data.enunciado, data.entrada, data.saida, data.gabarito, id)
        questaoDAO.add(newQuestao).map(qres => {
          val arquivosTeste: Option[Seq[FilePart[TemporaryFile]]] =
            request.body.asMultipartFormData.map(_.files)
          arquivosTeste.map { fileSeq =>
            fileSeq.filterNot(_.filename.isEmpty) map { f =>
              val (entrada, saida) = interpretarTeste(f.ref.file) match {
                case ("", s) => (None, s)
                case (e, s) => (Some(e), s)
              }
              val newTeste = Teste(0, entrada, saida, qres.id)
              testeDAO.add(newTeste)
            }
          }
          Redirect(routes.ProfessorController.questoes(id))
        })
      })
  }
}
