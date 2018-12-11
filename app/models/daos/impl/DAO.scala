package models.daos.impl

import play.api.db.slick.HasDatabaseConfigProvider
import utils.PostgresDriver

trait DAO extends BDefinicoesTabela with HasDatabaseConfigProvider[PostgresDriver]

