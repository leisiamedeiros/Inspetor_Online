package modules

import jobs.{ AuthTokenCleaner, Scheduler }
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

class JobModule extends ScalaModule with AkkaGuiceSupport {
  override def configure() = {
    bindActor[AuthTokenCleaner]("auth-token-cleaner")
    bind[Scheduler].asEagerSingleton()
  }
}
