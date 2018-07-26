with now_ts as (
  select current_timestamp at time zone 'America/Los_Angeles' as now_ts
), now as (
  select cast((select now_ts from now_ts) as date) as now
), date as (
  select
    (select now from now) today,
    case day_of_week((select now_ts from now_ts)) % 7
      when 1 then (select now from now) - interval '3' day
      when 0 then (select now from now) - interval '2' day
      else        (select now from now) - interval '1' day
    end as yesterday
), _portfolio as (
  select
    dataset,
    ticker,
    cast(quantity as decimal(10,4))      as quantity,
    cast(cost_per_share as decimal(6,2)) as cost_per_share
  from
    dw.portfolio
), _equities as (
  select
    dataset,
    ticker,
    cast(date as date)                     as date,
    try_cast(open as decimal(10,2))        as open,
    try_cast(close as decimal(10,2))       as close,
    try_cast(low as decimal(10,2))         as low,
    try_cast(high as decimal(10,2))        as high,
    try_cast(volume as decimal(20,2))      as volume,
    try_cast(split_ratio as decimal(10,2)) as split_ratio,
    try_cast(adj_open as decimal(10,2))    as adj_open,
    try_cast(adj_close as decimal(10,2))   as adj_close,
    try_cast(adj_low as decimal(10,2))     as adj_low,
    try_cast(adj_volume as decimal(20,2))  as adj_volume,
    try_cast(ex_dividend as decimal(10,2)) as ex_dividend
  from
    dw.equities
  where
    s3uploaddate between cast((select yesterday from date) as date)
                 and     cast((select today from date) as date)
), today as (
  select
    markets.description,
    markets.ticker,
    sum((quantity * cost_per_share))                 cost_basis,
    sum((quantity * coalesce(close,cost_per_share))) market_value,
    sum(((quantity * coalesce(close,cost_per_share)) - (quantity * cost_per_share))) gain_loss
  from
    _equities equities
    right join _portfolio portfolio on equities.dataset = portfolio.dataset and equities.ticker = portfolio.ticker
    join dw.markets on portfolio.dataset = markets.dataset and portfolio.ticker = markets.ticker
  where
    date in ( select today from date )
    or (case when markets.ticker in ('VGWAX') and date is null then 1 else 0 end)
       = 1
    or (case when markets.ticker in ('VMMXX') and date is null then 1 else 0 end)
       = 1
  group by
    1,2
), yesterday as (
  select
    markets.description,
    markets.ticker,
    sum((quantity * coalesce(close,cost_per_share))) yesterday
  from
    _equities equities
    right join _portfolio portfolio on equities.dataset = portfolio.dataset and equities.ticker = portfolio.ticker
    join dw.markets on portfolio.dataset = markets.dataset and portfolio.ticker = markets.ticker
  where
    date in ( select yesterday from date ) or date is null
  group by
    1,2
), detail as (
  select
    today.description,
    today.cost_basis, today.market_value, today.gain_loss,
    today.market_value - yesterday.yesterday today_gain_loss
  from
    today
    full outer join yesterday on today.ticker = yesterday.ticker
  order by today.market_value desc
), summary as (
  select
    'Portfolio Total'       description,
    sum(cost_basis)         cost_basis,
    sum(market_value)       market_value,
    sum(gain_loss)          gain_loss,
    sum(today_gain_loss)    today_gain_loss
  from
    detail
), _union as (
  select * from summary
  union all
  select * from detail
), report as (
  select
    description, cast(cost_basis as integer) cost_basis, cast(market_value as integer) market_value,
    cast(today_gain_loss as integer) today_gain_loss,
    cast(cast((today_gain_loss / market_value * 100) as decimal(8,2)) as varchar) || '%'  "today_gain_loss_%",
    cast(gain_loss as integer) total_gain_loss,
    cast(cast((gain_loss / cost_basis * 100) as decimal(8,2)) as varchar) || '%'  "total_gain_loss_%"
  from
    _union
)
select * from report order by market_value desc
