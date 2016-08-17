package model

import play.api.Play
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class Lista(id: Long, nome: String, assunto: String)

case class ListaFormData(nome: String, assunto: String)

case class Questao(id: Long, numero: Long, enunciado: String, gabarito: String, entrada: String, saida: String, idLista: Long)

case class QuestaoFormData(numero: Long, enunciado: String, gabarito: String, entrada: String, saida: String)

case class Aluno(id: Long, lista: Long, questao: Long, resposta: String)

case class AlunoFormData(questao: Long, resposta: String)

object ListaForm {
  val form = Form(
    mapping(
      "nome" -> nonEmptyText,
      "assunto" -> nonEmptyText
    )(ListaFormData.apply)(ListaFormData.unapply)
  )
}

object QuestaoForm {
  val form = Form(
    mapping(
      "numero" -> longNumber,
      "enunciado" -> nonEmptyText,
      "gabarito" -> nonEmptyText,
      "entrada" -> nonEmptyText,
      "saida" -> nonEmptyText
    )(QuestaoFormData.apply)(QuestaoFormData.unapply)
  )
}

object AlunoForm {
  val form = Form(
    mapping(
      "questao" -> longNumber,
      "resposta" -> nonEmptyText
    )(AlunoFormData.apply)(AlunoFormData.unapply)
  )
}

class ListaTable(tag: Tag) extends Table[Lista](tag, "lista") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def nome = column[String]("nome")
  def assunto = column[String]("assunto")
  override def * = (id,nome,assunto) <>(Lista.tupled, Lista.unapply)
}

class QuestaoTable(tag: Tag) extends Table[Questao](tag, "questao") {
  val lista = TableQuery[ListaTable]
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def numero = column[Long]("numero")
  def enunciado = column[String]("enunciado")
  def gabarito = column[String]("gabarito")
  def entrada = column[String]("entrada")
  def saida = column[String]("saida")
  def idLista = column[Long]("idLista")
  override def * = (id,numero,enunciado,gabarito,entrada,saida,idLista) <>(Questao.tupled, Questao.unapply)

  def questaolista = foreignKey("fk_questao_lista",idLista, lista)(_.id)
}

class AlunoTable(tag: Tag) extends Table[Aluno](tag, "aluno") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def lista = column[Long]("lista")
  def questao = column[Long]("questao")
  def resposta = column[String]("resposta")
  override def * = (id,lista,questao,resposta) <>(Aluno.tupled, Aluno.unapply)

}


object ListasQuestoes {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  val listas = TableQuery[ListaTable]
  val questoes = TableQuery[QuestaoTable]
  val aluno = TableQuery[AlunoTable]

  def addlista(lista: Lista): Future[String] = {
    dbConfig.db.run(listas += lista).map(res => "Lista successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def addquestao(questao: Questao): Future[String] = {
    dbConfig.db.run(questoes += questao).map(res => "Questao successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def addresposta(resposta: Aluno): Future[String] = {
    dbConfig.db.run(aluno += resposta).map(res => "Questao successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def listAll: Future[Seq[Lista]] = {
    dbConfig.db.run(listas.result)
  }

  def listquestion: Future[Seq[Questao]] = {
    dbConfig.db.run(questoes.result)
  }

  def listquestionaluno: Future[Seq[Aluno]] = {
    dbConfig.db.run(aluno.result)
  }

  def getlista(id: Long): Future[Option[Lista]] = {
    dbConfig.db.run(listas.filter(_.id === id).result.headOption)
  }
}
    
