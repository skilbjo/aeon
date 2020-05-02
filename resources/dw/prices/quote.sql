select
  *
from
  dw.:table
where
  ticker   = upper(':ticker')
  and date = ':date'
limit 5
;
