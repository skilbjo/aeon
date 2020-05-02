select
  ticker,
  currency,
  date,
  avg(rate)          open,
  avg(high)          high,
  avg(low)           low,
  min(dw_created_at) dw_created_at
from
  dw.:table
where
  currency = upper(':ticker')
  and date = ':date'
group by
  1, 2, 3
;
