select
  *
from
  dw.:table
where
  ticker = :ticker
  and date = :date
limit 5
;
