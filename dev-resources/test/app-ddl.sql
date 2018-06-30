begin;

  create schema if not exists aoin;

  create table if not exists aoin.users (
    id          uuid primary key default uuid_generate_v4()
    username    text,
    password    text
  );

  insert into aoin.user (username, password) values
    ('skilbjo','god')
  ;

commit;
