with date as (
  select (now() at time zone 'pst')::date now
)
select
  currency, date, rate
from
  dw.currency
where
  currency in ('GBP', 'EUR')
  and date between ( select now - interval '3 day' from date )
           and ( select now from date )
group by
  currency, date, rate
limit 4
