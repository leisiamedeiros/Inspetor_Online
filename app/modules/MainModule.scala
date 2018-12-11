package modules

import com.google.inject.AbstractModule

import models.daos.api.{ ListaDAO, QuestaoDAO, RespostaDAO, TesteDAO }
import models.daos.impl.{ ListaDAOImpl, QuestaoDAOImpl, RespostaDAOImpl, TesteDAOImpl }
import net.codingwell.scalaguice.ScalaModule

class MainModule extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[ListaDAO].to[ListaDAOImpl]
    bind[QuestaoDAO].to[QuestaoDAOImpl]
    bind[TesteDAO].to[TesteDAOImpl]
    bind[RespostaDAO].to[RespostaDAOImpl]
  }

}
