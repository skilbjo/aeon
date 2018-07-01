begin;

  create extension "uuid-ossp";
  create extension pgcrypto;

  create schema if not exists aoin;

  create table if not exists aoin.users (
    id          uuid primary key default uuid_generate_v4(),
    username    text,
    password    text
  );

  insert into aoin.users (username, password) values
    ('skilbjo', encode(digest('god','sha256'),'hex'))
  ;

commit;
