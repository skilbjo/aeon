-- Dimension table
begin;

  create schema if not exists dw;

  drop table if exists dw.markets cascade;
  create table if not exists dw.markets (
    dataset         text,
    ticker          text,
    description     text,

    constraint markets_pk primary key (dataset, ticker)
  );

  truncate dw.markets cascade;
  insert into dw.markets values
    ('CURRFX',     'EURUSD',          'Value of 1 EUR in USD'),
    ('CURRFX',     'GBPUSD',          'Value of 1 GBP in USD')
  ;
commit;

-- Fact tables
begin;

  drop table if exists dw.currency;
  create table if not exists dw.currency (
    dataset         text,
    ticker          text,
    currency        text,
    date            date,
    rate            decimal(24,14),
    high_est        decimal(24,14),
    low_est         decimal(24,14),

    constraint currency_pk primary key (dataset, ticker, date),
    constraint currency_markets_fk foreign key (dataset, ticker) references dw.markets (dataset, ticker)
  );

commit;
