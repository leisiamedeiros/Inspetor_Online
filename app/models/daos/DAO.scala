package models.daos

import slick.driver.PostgresDriver
import play.api.db.slick.HasDatabaseConfigProvider

trait DAO extends BDefinicoesTabela with HasDatabaseConfigProvider[PostgresDriver]

