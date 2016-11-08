package controllers

import javax.inject.Inject
import java.io.File
import scala.io.Source

import play.api._
import play.api.Logger
import play.api.mvc._
import play.api.mvc.MultipartFormData.FilePart
import play.api.i18n.{ MessagesApi, I18nSupport }
import play.api.libs.Files.TemporaryFile
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Await }
import models.{ Lista, Questao, Teste, Usuario }
import models.daos.api.{ ListaDAO, QuestaoDAO, TesteDAO }
import forms.{ ListaForm, QuestaoForm }

import com.mohiva.play.silhouette.api.Silhouette
import utils.auth.{ DefaultEnv, WithRole }

class ProfessorController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  listaDAO: ListaDAO,
  questaoDAO: QuestaoDAO,
  testeDAO: TesteDAO
) extends Controller with I18nSupport {

  def listas = silhouette.SecuredAction(WithRole("professor")).async { implicit request =>
    val usuario: Usuario = request.identity
    listaDAO.getByProfessor(usuario.id) map { listas =>
      Ok(views.html.professor.listas(listas, request.identity))
    }
  }

  def novaLista = silhouette.SecuredAction(WithRole("professor")).async { implicit request =>
    val usuario: Usuario = request.identity
    listaDAO.getByProfessor(usuario.id) map { listas =>
      Ok(views.html.professor.novalista(ListaForm.form, request.identity, listas))
    }
  }

  def createLista = silhouette.SecuredAction(WithRole("professor")).async { implicit request =>
    ListaForm.form.bindFromRequest.fold(
      errors => Future(BadRequest),
      data => {
        val newLista = Lista(0, data.nome, data.assunto, request.identity.id)
        listaDAO.add(newLista).map(res =>
          Redirect(routes.ProfessorController.listas())
        )
      })
  }

  def questoes(id: Int) = silhouette.SecuredAction(WithRole("professor")).async { implicit request =>
    val usuario: Usuario = request.identity
    listaDAO.getByProfessor(usuario.id) map { listas =>
      Await.result(questaoDAO.list map { q =>
        Ok(views.html.professor.questoes(id, q, request.identity, listas))
      }, Duration.Inf)
    }
  }

  def novaQuestao(id: Int) = silhouette.SecuredAction(WithRole("professor")).async { implicit request =>
    val usuario: Usuario = request.identity
    listaDAO.getByProfessor(usuario.id) map { listas =>
      Ok(views.html.professor.novaquestao(id, QuestaoForm.form, request.identity, listas))
    }
  }

  def interpretarTeste(teste: File): Array[String] = {
    var resultado = Array("", "")
    var i = 0
    for (linha <- Source.fromFile(teste).getLines()) {
      linha match {
        case "#entrada" => i = 0
        case "#saida" => i = 1
        case texto => resultado(i) += texto + "\n"
      }
    }
    return resultado
  }

  def createQuestao(id: Int) = silhouette.SecuredAction(WithRole("professor")).async { implicit request =>
    QuestaoForm.form.bindFromRequest.fold(
      errors => Future(BadRequest),
      data => {
        val newQuestao = Questao(0, data.numero, data.enunciado, data.entrada, data.saida, data.gabarito, id)
        questaoDAO.add(newQuestao).map(qres => {
          val arquivosTeste: Option[Seq[FilePart[TemporaryFile]]] =
            request.body.asMultipartFormData.map(_.files)
          arquivosTeste.map { fileSeq =>
            fileSeq.filterNot(_.filename == "") map { f =>
              val (entrada, saida) = interpretarTeste(f.ref.file) match {
                case Array(e: String, s: String) => (Some(e), s)
                case Array("", s: String) => (None, s)
                case _ => (None, "")
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
