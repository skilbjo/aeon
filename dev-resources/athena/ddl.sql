create schema dw;

drop table if exists dw.currency;
create external table dw.currency (
  date            string,
  rate            string,
  high_est        string,
  low_est         string,
  dataset         string,
  ticker          string,
  currency        string
)
partitioned by (
  s3uploaddate date
)
row format serde
  'org.apache.hadoop.hive.serde2.OpenCSVSerde'
stored as inputformat
  'org.apache.hadoop.mapred.TextInputFormat'
outputformat
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
location
  's3://skilbjo-data/datalake/markets-etl/currency'
tblproperties (
  "skip.header.line.count"="1"
);

drop table if exists dw.economics;
create external table dw.economics (
  date            string,
  ..
  dataset         string,
  ticker          string,
)
partitioned by (
  s3uploaddate date
)
row format serde
  'org.apache.hadoop.hive.serde2.OpenCSVSerde'
stored as inputformat
  'org.apache.hadoop.mapred.TextInputFormat'
outputformat
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
location
  's3://skilbjo-data/datalake/markets-etl/economics'
tblproperties (
  "skip.header.line.count"="1"
);

drop table if exists dw.equities;
create external table dw.equities (
  date            string,
  ..
  dataset         string,
  ticker          string,
)
partitioned by (
  s3uploaddate date
)
row format serde
  'org.apache.hadoop.hive.serde2.OpenCSVSerde'
stored as inputformat
  'org.apache.hadoop.mapred.TextInputFormat'
outputformat
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
location
  's3://skilbjo-data/datalake/markets-etl/equities'
tblproperties (
  "skip.header.line.count"="1"
);

drop table if exists dw.interest_rates;
create external table dw.interest_rates (
  date            string,
  ..
  dataset         string,
  ticker          string,
)
partitioned by (
  s3uploaddate date
)
row format serde
  'org.apache.hadoop.hive.serde2.OpenCSVSerde'
stored as inputformat
  'org.apache.hadoop.mapred.TextInputFormat'
outputformat
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
location
  's3://skilbjo-data/datalake/markets-etl/interest_rates'
tblproperties (
  "skip.header.line.count"="1"
);

drop table if exists dw.real_estate;
create external table dw.real_estate (
  date            string,
  ..
  dataset         string,
  ticker          string,
)
partitioned by (
  s3uploaddate date
)
row format serde
  'org.apache.hadoop.hive.serde2.OpenCSVSerde'
stored as inputformat
  'org.apache.hadoop.mapred.TextInputFormat'
outputformat
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
location
  's3://skilbjo-data/datalake/markets-etl/real_estate'
tblproperties (
  "skip.header.line.count"="1"
);

msck repair table dw.currency;
msck repair table dw.economics;
msck repair table dw.equities;
msck repair table dw.interest_rates;
msck repair table dw.real_estate;

with _currency as (
  select
    dataset,
    ticker,
    currency,
    cast(date as date)               as date,
    cast(rate as decimal(24,14))     as rate,
    cast(high_est as decimal(24,14)) as high_est,
    cast(low_est as decimal(24,14))  as low_est
  from
    dw.currency
)
select *
from _currency

with _economics as (
  select
    dataset,
    ticker,
    cast(date as date)               as date,
    ...
  from
    dw.economics
)
select *
from _economics

with _equities as (
  select
    dataset,
    ticker,
    cast(date as date)               as date,
    ...
  from
    dw.equities
)
select *
from _equities

with _interest_rates as (
  select
    dataset,
    ticker,
    cast(date as date)               as date,
    ...
  from
    dw.interest_rates
)
select *
from _interest_rates

with _real_estate as (
  select
    dataset,
    ticker,
    cast(date as date)               as date,
    ...
  from
    dw.real_estate
)
select *
from _real_estate
