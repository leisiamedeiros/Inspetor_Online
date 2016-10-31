package models.daos.api

import com.mohiva.play.silhouette.api.LoginInfo
import java.util.UUID
import org.joda.time._
import utils.PostgresDriver
import slick.lifted.ProvenShape.proveShapeOf

trait BDefinicoesTabela {

  protected val driver: PostgresDriver
  import driver.api._

  case class BDUsuario(
    id: UUID,
    papel: String,
    nomeCompleto: String,
    email: String,
    avatarURL: Option[String],
    ativado: Boolean
  )

  // Tabela de Usuarios
  class Usuarios(tag: Tag) extends Table[BDUsuario](tag, "usuarios") {
    def id = column[UUID]("id", O.PrimaryKey)
    def papel = column[String]("papel")
    def nomeCompleto = column[String]("nome_completo")
    def email = column[String]("email")
    def avatarURL = column[Option[String]]("avatar_url")
    def ativado = column[Boolean]("ativado")
    override def * = (id, papel, nomeCompleto, email, avatarURL, ativado) <> (BDUsuario.tupled, BDUsuario.unapply)
  }

  case class BDLoginInfo(
    id: Long,
    providerID: String,
    providerKey: String
  )

  class LoginInfos(tag: Tag) extends Table[BDLoginInfo](tag, "login_infos") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def providerID = column[String]("provider_id")
    def providerKey = column[String]("provider_key")
    override def * = (id, providerID, providerKey) <> (BDLoginInfo.tupled, BDLoginInfo.unapply)
  }

  case class BDUsuarioLoginInfo(
    usuarioID: UUID,
    loginInfoId: Long
  )

  class UsuarioLoginInfos(tag: Tag) extends Table[BDUsuarioLoginInfo](tag, "usuarios_has_login_infos") {
    def usuarioID = column[UUID]("usuario_id")
    def loginInfoId = column[Long]("login_info_id")
    override def * = (usuarioID, loginInfoId) <> (BDUsuarioLoginInfo.tupled, BDUsuarioLoginInfo.unapply)

    def fkUsuario = foreignKey("fk_usuarios_has_login_infos_usuarios", usuarioID, usuarios)(_.id)
    def fkLoginInfo = foreignKey("fk_usuarios_has_login_infos_login_infos", loginInfoId, loginInfos)(_.id)
  }

  case class BDLista(
    id: Int,
    nome: String,
    assunto: String
  )

  // Tabela de Listas
  class Listas(tag: Tag) extends Table[BDLista](tag, "listas") {
    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def nome = column[String]("nome")
    def assunto = column[String]("assunto")
    override def * = (id, nome, assunto) <> (BDLista.tupled, BDLista.unapply)
  }

  case class BDQuestao(
    id: Int,
    numero: Int,
    enunciado: String,
    gabarito: String,
    idLista: Int
  )

  // Tabela de Questoes
  class Questoes(tag: Tag) extends Table[BDQuestao](tag, "questoes") {
    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def numero = column[Int]("numero")
    def enunciado = column[String]("enunciado")
    def gabarito = column[String]("gabarito")
    def idLista = column[Int]("lista_id")
    override def * = (id, numero, enunciado, gabarito, idLista) <> (BDQuestao.tupled, BDQuestao.unapply)

    def fkidLista = foreignKey("fk_questoes_listas", idLista, listas)(_.id)
  }

  case class BDTeste(
    id: Int,
    entrada: Option[String],
    saida: String
  )

  // Tabela de Testes
  class Testes(tag: Tag) extends Table[BDTeste](tag, "testes") {
    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def entrada = column[Option[String]]("entrada")
    def saida = column[String]("saida")
    override def * = (id, entrada, saida) <> (BDTeste.tupled, BDTeste.unapply)
  }

  case class BDResposta(
    id: Int,
    dados: String, // FIXME: Change to BLOB
    estado: String,
    nota: Option[Float]
  )

  // Tabela de Respostas
  class Respostas(tag: Tag) extends Table[BDResposta](tag, "respostas") {
    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def dados = column[String]("dados")
    def estado = column[String]("estado")
    def nota = column[Option[Float]]("nota")
    override def * = (id, dados, estado, nota) <> (BDResposta.tupled, BDResposta.unapply)
  }

  case class BDAuthToken(
    id: UUID,
    usuarioID: UUID,
    expiry: DateTime
  )

  // Tabela de AuthTokens
  class AuthTokens(tag: Tag) extends Table[BDAuthToken](tag, "auth_tokens") {
    def id = column[UUID]("id", O.PrimaryKey)
    def usuarioID = column[UUID]("usuario_id")
    def expiry = column[DateTime]("expiry")
    override def * = (id, usuarioID, expiry) <> (BDAuthToken.tupled, BDAuthToken.unapply)

    def fkUsuario = foreignKey("fk_auth_tokens_usuarios", usuarioID, usuarios)(_.id)
  }

  case class BDPasswordInfo(
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: Long
  )

  // Tabela de PasswordInfos
  class PasswordInfos(tag: Tag) extends Table[BDPasswordInfo](tag, "password_infos") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("login_info_id")
    override def * = (hasher, password, salt, loginInfoId) <> (BDPasswordInfo.tupled, BDPasswordInfo.unapply)

    def fkLoginInfo = foreignKey("fk_password_infos_login_infos", loginInfoId, loginInfos)(_.id)
  }

  case class BDOAuth1Info(
    id: Long,
    token: String,
    secret: String,
    loginInfoId: Long
  )

  // Tabela de OAuth1Infos
  class OAuth1Infos(tag: Tag) extends Table[BDOAuth1Info](tag, "oauth1_infos") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def token = column[String]("token")
    def secret = column[String]("secret")
    def loginInfoId = column[Long]("login_info_id")
    override def * = (id, token, secret, loginInfoId) <> (BDOAuth1Info.tupled, BDOAuth1Info.unapply)

    def fkLoginInfo = foreignKey("fk_oauth1_infos_login_infos", loginInfoId, loginInfos)(_.id)
  }

  case class BDOAuth2Info(
    id: Long,
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoId: Long
  )

  // Tabela de OAuth2Infos
  class OAuth2Infos(tag: Tag) extends Table[BDOAuth2Info](tag, "oauth2_infos") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accessToken = column[String]("access_token")
    def tokenType = column[Option[String]]("token_type")
    def expiresIn = column[Option[Int]]("expires_in")
    def refreshToken = column[Option[String]]("refresh_token")
    def loginInfoId = column[Long]("login_info_id")
    override def * = (id, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (BDOAuth2Info.tupled, BDOAuth2Info.unapply)

    def fkLoginInfo = foreignKey("fk_oauth2_infos_login_infos", loginInfoId, loginInfos)(_.id)
  }

  case class BDOpenIDInfo(
    id: String,
    loginInfoId: Long
  )

  // Tabela de OpenIDInfos
  class OpenIDInfos(tag: Tag) extends Table[BDOpenIDInfo](tag, "openid_infos") {
    def id = column[String]("id", O.PrimaryKey)
    def loginInfoId = column[Long]("login_info_id")
    override def * = (id, loginInfoId) <> (BDOpenIDInfo.tupled, BDOpenIDInfo.unapply)

    def fkLoginInfo = foreignKey("fk_openid_infos_login_infos", loginInfoId, loginInfos)(_.id)
  }

  case class BDOpenIDAttribute(
    id: String,
    key: String,
    value: String
  )

  // Tabela de OpenIDAttributes
  class OpenIDAttributes(tag: Tag) extends Table[BDOpenIDAttribute](tag, "openid_attribute") {
    def id = column[String]("id")
    def key = column[String]("key")
    def value = column[String]("value")
    override def * = (id, key, value) <> (BDOpenIDAttribute.tupled, BDOpenIDAttribute.unapply)
  }

  // Queries
  val usuarios = TableQuery[Usuarios]
  val loginInfos = TableQuery[LoginInfos]
  val usuarioLoginInfos = TableQuery[UsuarioLoginInfos]
  val listas = TableQuery[Listas]
  val questoes = TableQuery[Questoes]
  val testes = TableQuery[Testes]
  val respostas = TableQuery[Respostas]
  val authTokens = TableQuery[AuthTokens]
  val passwordInfos = TableQuery[PasswordInfos]
  val oauth1Infos = TableQuery[OAuth1Infos]
  val oauth2Infos = TableQuery[OAuth2Infos]
  val openIDInfos = TableQuery[OpenIDInfos]
  val openIDAttributes = TableQuery[OpenIDAttributes]

  def loginInfoQuery(loginInfo: LoginInfo) = loginInfos.filter(
    dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey
  )
}
