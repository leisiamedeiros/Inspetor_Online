package models.daos

import slick.driver.PostgresDriver
import slick.lifted.ProvenShape.proveShapeOf

trait BDefinicoesTabela {

  protected val driver: PostgresDriver
  import driver.api._

  case class BDUsuario (
    id: Int,
    papel: String,
    nomeCompleto: String,
    email: String,
    avatarUrl: Option[String]
  )

  // Tabela de Usuarios
  class Usuarios(tag: Tag) extends Table[DBUsuario](tag, "usuarios") {
    def id = column[String]("id", O.PrimaryKey)
    def papel = column[String]("papel")
    def nomeCompleto = column[String]("nome_completo")
    def email = column[String]("email")
    def avatarUrl = column[Option[String]]("avatar_url")
    def * = (id, papel, nomeCompleto, email, avatarUrl) <> (BDUsuario.tupled, BDUsuario.unapply)
  }

  case class BDLista(
    id: Int,
    nome: String,
    assunto: String
  )

  // Tabela de Listas
  class Listas(tag: Tag) extends Table[BDLista](tag, "listas") {
    def id = column[String]("id", O.PrimaryKey)
    def nome = column[String]("nome")
    def assunto = column[String]("assunto")
    def * = (id, nome, assunto) <> (BDLista.tupled, BDLista.unapply)
  }

  case class BDQuestao(
    id: Int,
    numero: Int,
    enunciado: String
  )

  // Tabela de Questoes
  class Questoes(tag: Tag) extends Table[BDQuestao](tag, "questoes") {
    def id = column[String]("id", O.PrimaryKey)
    def numero = column[Int]("numero")
    def enunciado = column[String]("enunciado")
    def * = (id, numero, enunciado) <> (BDQuestao.tupled, BDQuestao.unapply)
  }

  case class BDTeste(
    id: Int,
    entrada: Option[String],
    saida: String
  )

  // Tabela de Testes
  class Testes(tag: Tag) extends Table[BDTeste](tag, "testes") {
    def id = column[String]("id", O.PrimaryKey)
    def entrada = column[Option[String]]("entrada")
    def saida = column[String]("saida")
    def * = (id, entrada, saida) <> (BDTeste.tupled, BDTeste.unapply)
  }

  case class BDResposta(
    id: Int,
    dados: String, // FIXME: Change to BLOB
    estado: String,
    nota: Option[Float]
  )

  // Tabela de Respostas
  class Respostas(tag: Tag) extends Table[BDResposta](tag, "respostas") {
    def id = column[String]("id", O.PrimaryKey)
    def dados = column[String]("dados")
    def estado = column[String]("estado")
    def nota = column[Option[Float]]("nota")
    def * = (id, dadaos, estado, nota) <> (BDResposta.tupled, BDResposta.unapply)
  }

  // Queries
  val usuarios = TableQuery[Usuarios]
  val listas = TableQuery[Listas]
  val questoes = TableQuery[Questoes]
  val testes = TableQuery[Testes]
  val respostas = TableQuery[Respostas]
}
