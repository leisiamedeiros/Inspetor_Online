package models.daos.api

import utils.PostgresDriver
import play.api.db.slick.HasDatabaseConfigProvider

trait DAO extends BDefinicoesTabela with HasDatabaseConfigProvider[PostgresDriver]

