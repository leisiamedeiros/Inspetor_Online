# -- 09-10-2016 Rev

# --- !Ups

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE usuarios (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  papel VARCHAR(20) NOT NULL,
  nome_completo VARCHAR(255) NOT NULL,
  email VARCHAR(45) NOT NULL,
  avatar_url TEXT NULL,
  ativado BOOLEAN NOT NULL
);

CREATE TABLE login_infos (
  id SERIAL PRIMARY KEY,
  provider_id varchar(100) NOT NULL,
  provider_key varchar(100) NOT NULL
);

CREATE TABLE usuarios_has_login_infos (
  usuario_id UUID NOT NULL,
  login_info_id INTEGER NOT NULL,
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
  usuario_id UUID NOT NULL,
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
  linguagem VARCHAR(100),
  dados TEXT NOT NULL,
  estado CHAR(2) NOT NULL,
  nota FLOAT NULL,
  usuario_id UUID NOT NULL,
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

CREATE TABLE auth_tokens (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  usuario_id UUID NOT NULL,
  expiry timestamptz NOT NULL,
  CONSTRAINT fk_auth_tokens_usuarios
    FOREIGN KEY (usuario_id)
    REFERENCES usuarios (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE password_infos (
  hasher VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  salt VARCHAR(255) NULL,
  login_info_id BIGINT NOT NULL,
  CONSTRAINT fk_password_infos_login_infos
    FOREIGN KEY (login_info_id)
    REFERENCES login_infos (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE oauth1_infos (
  id SERIAL PRIMARY KEY,
  token VARCHAR(255) NOT NULL,
  secret VARCHAR(255) NOT NULL,
  login_info_id BIGINT NOT NULL,
  CONSTRAINT fk_password_infos_login_infos
    FOREIGN KEY (login_info_id)
    REFERENCES login_infos (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE oauth2_infos (
  id SERIAL PRIMARY KEY,
  access_token VARCHAR(255) NOT NULL,
  token_type VARCHAR(255) NOT NULL,
  expires_in INT NULL,
  refresh_token VARCHAR(255) NULL,
  login_info_id BIGINT NOT NULL,
  CONSTRAINT fk_password_infos_login_infos
    FOREIGN KEY (login_info_id)
    REFERENCES login_infos (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE openid_infos (
  id VARCHAR(255) PRIMARY KEY,
  login_info_id BIGINT NOT NULL,
  CONSTRAINT fk_openid_infos_login_infos
    FOREIGN KEY (login_info_id)
    REFERENCES login_infos (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE openid_attributes (
  id VARCHAR(255) PRIMARY KEY,
  key VARCHAR(255) NOT NULL,
  value VARCHAR(255) NOT NULL
);

CREATE TABLE testes (
  id SERIAL PRIMARY KEY,
  entrada TEXT NULL,
  saida TEXT NOT NULL,
  questao_id INTEGER NOT NULL,
  CONSTRAINT fk_testes_questoes
    FOREIGN KEY (questao_id)
    REFERENCES questoes (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

# --- !Downs

DROP TABLE testes;

DROP TABLE openid_attributes;

DROP TABLE openid_infos;

DROP TABLE oauth2_infos;

DROP TABLE oauth1_infos;

DROP TABLE password_infos;

DROP TABLE auth_tokens;

DROP TABLE respostas;

DROP TABLE questoes;

DROP TABLE listas;

DROP TABLE usuarios_has_login_infos;

DROP TABLE login_infos;

DROP TABLE usuarios;
