# -- 09-10-2016 Rev

# --- !Ups

CREATE TABLE usuarios (
  id SERIAL PRIMARY KEY,
  papel VARCHAR(20) NOT NULL,
  nome_completo VARCHAR(255) NOT NULL,
  email VARCHAR(45) NOT NULL,
  avatar_url TEXT NULL
);

CREATE TABLE login_infos (
  id SERIAL PRIMARY KEY,
  provider_id varchar(100) NOT NULL,
  provider_key varchar(100) NOT NULL
);

CREATE TABLE usuarios_has_login_infos (
  usuario_id INTEGER NOT NULL,
  login_info_id INTEGER NOT NULL,
  CONSTRAINT fk_usuarios_has_login_infos_login_infos_idx
    UNIQUE(login_info_id),
  CONSTRAINT fk_usuarios_has_login_infos_usuarios
    FOREIGN KEY (usuario_id)
    REFERENCES usuarios (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_usuarios_has_login_infos_login_infos
    FOREIGN KEY (login_info_id)
    REFERENCES login_infos (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE listas (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(45) NOT NULL,
  assunto VARCHAR(80) NOT NULL,
  usuario_id INTEGER NOT NULL UNIQUE,
  CONSTRAINT fk_listas_usuarios_idx
    UNIQUE(usuario_id),
  CONSTRAINT fk_listas_usuarios
    FOREIGN KEY (usuario_id)
    REFERENCES usuarios (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE questoes (
  id SERIAL PRIMARY KEY,
  numero INTEGER NOT NULL,
  enunciado TEXT NOT NULL,
  entrada TEXT NOT NULL,
  saida TEXT NOT NULL,
  gabarito TEXT NOT NULL,
  lista_id INTEGER NOT NULL,
  CONSTRAINT fk_questoes_listas
      FOREIGN KEY (lista_id)
      REFERENCES listas (id)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);

CREATE TABLE respostas (
  id SERIAL PRIMARY KEY,
  dados BYTEA NOT NULL,
  estado CHAR(2) NOT NULL,
  nota FLOAT NULL,
  usuario_id INTEGER NOT NULL,
  questao_id INTEGER NOT NULL,
  CONSTRAINT fk_respostas_usuarios
    FOREIGN KEY (usuario_id)
    REFERENCES usuarios (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_respostas_questoes
    FOREIGN KEY (questao_id)
    REFERENCES questoes (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

# --- !Downs

DROP TABLE respostas;

DROP TABLE questoes;

DROP TABLE listas;

DROP TABLE usuarios_has_login_infos;

DROP TABLE login_infos;

DROP TABLE usuarios;

