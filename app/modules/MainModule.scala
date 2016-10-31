package modules

import com.google.inject.AbstractModule
import java.time.Clock
import net.codingwell.scalaguice.ScalaModule

import models.daos.api.{ListaDAO}
import models.daos.impl.{ListaDAOImpl}

class MainModule extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[ListaDAO].to[ListaDAOImpl]
  }

}
