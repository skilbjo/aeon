with now as (
  select (now() at time zone 'pst')::date now
), date as (
  select
    (select now from now) today,
    case (extract(isodow from (select now from now))::integer) % 7
      when 1 then (select now from now) - 3
      when 0 then (select now from now) - 2
      else        (select now from now) - 1
    end as yesterday
), today as (
  select
    markets.description,
    markets.ticker,
    sum((quantity * cost_per_share))                 cost_basis,
    sum((quantity * coalesce(close,cost_per_share))) market_value,
    sum(((quantity * coalesce(close,cost_per_share)) - (quantity * cost_per_share))) gain_loss
  from
    dw.equities_fact equities
    right join dw.portfolio_dim portfolio on equities.dataset = portfolio.dataset and equities.ticker = portfolio.ticker
    join dw.markets_dim markets on portfolio.dataset = markets.dataset and portfolio.ticker = markets.ticker
  where
    date in ( select today from date )
    or (case when markets.ticker in ('VGWAX') and date is null then 1 else 0 end)
       = 1
    or (case when markets.ticker in ('VMMXX') and date in (select yesterday from date) then 1 else 0 end)
       = 1
  group by
    1,2
), yesterday as (
  select
    markets.description,
    markets.ticker,
    sum((quantity * coalesce(close,cost_per_share))) yesterday
  from
    dw.equities_fact equities
    right join dw.portfolio_dim portfolio on equities.dataset = portfolio.dataset and equities.ticker = portfolio.ticker
    join dw.markets_dim markets on portfolio.dataset = markets.dataset and portfolio.ticker = markets.ticker
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
    'Portfolio Total'::text description,
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
    description, cost_basis::int , market_value::int,
    today_gain_loss::int,
    (today_gain_loss / market_value * 100)::decimal(8,2) "today_gain_loss_%",
    gain_loss::int total_gain_loss,
    (gain_loss / cost_basis * 100)::decimal(8,2)         "total_gain_loss_%"
  from
    _union
)
select * from report
