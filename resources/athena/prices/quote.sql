select
  *
from
  datalake.:table
where
  ticker   = ':ticker'
  and date = ':date'
limit 5
;
