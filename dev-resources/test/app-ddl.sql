begin;

  create extension "uuid-ossp";
  create extension pgcrypto;

  create schema if not exists aeon;

  create table if not exists aeon.users (
    id          uuid primary key default uuid_generate_v4(),
    username    text,
    password    text
  );

  insert into aeon.users (username, password) values
    ('skilbjo', encode(digest('god','sha256'),'hex'))
  ;

commit;
