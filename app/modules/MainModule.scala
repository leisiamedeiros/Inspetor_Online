package modules

import com.google.inject.AbstractModule
import java.time.Clock
import net.codingwell.scalaguice.ScalaModule

import models.daos.api._
import models.daos.impl._

class MainModule extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[ListaDAO].to[ListaDAOImpl]
    bind[QuestaoDAO].to[QuestaoDAOImpl]
    bind[TesteDAO].to[TesteDAOImpl]
    bind[RespostaDAO].to[RespostaDAOImpl]
  }

}
