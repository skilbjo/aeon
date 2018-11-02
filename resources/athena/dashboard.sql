with date as (
  select cast((current_timestamp at time zone 'America/Los_Angeles') as date) now
), _currency as (
  select
    dataset,
    ticker,
    currency,
    cast(date as date)               as date,
    cast(rate as decimal(24,14))     as rate,
    cast(high as decimal(24,14)) as high,
    cast(low as decimal(24,14))  as low
  from
    dw.currency_fact currency
), data as (
  select
    row_number() over (partition by currency order by date desc) as rn,
    currency, date, rate
  from
    _currency
  where
    currency in ('GBP', 'EUR')
    and date between ( select now - interval '30' day from date )
             and     ( select now from date )
  group by
    currency, date, rate
)
select
  currency,
  date,
  rate
from
  data
where
  rn in (1,2)
group by
  currency,
  date,
  rate
order by
  currency, date desc
;
