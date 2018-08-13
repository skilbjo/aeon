begin;

  create extension if not exists "uuid-ossp";
  create extension if not exists pgcrypto;

  create schema if not exists aeon;

  drop table if exists aeon.users;
  create table if not exists aeon.users (
    id          uuid primary key default uuid_generate_v4(),
    _user       text unique not null,
    password    text not null
  );

  insert into aeon.users (_user, password) values
    ('skilbjo', encode(digest('god','sha256'),'hex'))
  ;

commit;