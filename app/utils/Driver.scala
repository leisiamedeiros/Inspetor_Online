package utils

import slick.driver.{ PostgresDriver => SlickPostgresDriver }
import com.github.tminglei.slickpg._

trait PostgresDriver extends SlickPostgresDriver
  with PgDateSupportJoda {
  override val api = new API with DateTimeImplicits
  val plainAPI = new API with JodaDateTimePlainImplicits
}

object PostgresDriver extends PostgresDriver
