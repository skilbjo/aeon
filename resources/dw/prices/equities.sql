select
  ticker,
  date,
  avg(open)          open,
  avg(close)         "close",
  avg(low)           low,
  avg(high)          high,
  avg(volume)        volume,
  avg(split_ratio)   split_ratio,
  avg(adj_open)      adj_open,
  avg(adj_close)     adj_close,
  avg(adj_low)       adj_low,
  avg(adj_high)      adj_high,
  avg(adj_volume)    adj_volume,
  avg(ex_dividend)   ex_dividend,
  min(dw_created_at) dw_created_at
from
  dw.:table
where
  ticker   = upper(':ticker')
  and date = ':date'
group by
  1, 2
;
