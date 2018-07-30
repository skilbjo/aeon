with date as (
  select (now() at time zone 'pst')::date now
), data as (
  select
    row_number() over (partition by currency order by date desc) as rn,
    currency, date, rate
  from
    dw.currency_fact currency
  where
    currency in ('GBP', 'EUR')
    and date between ( select now - interval '30 day' from date )
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
