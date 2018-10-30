with now as (
  select (now() at time zone 'pst')::date now
), _user as (
  select ':user'::text as _user
), datasource as (
  select 'ALPHA-VANTAGE'::text as datasource
), date as (
  select
    (select now from now) today,
    case (extract(isodow from (select now from now))::integer) % 7
      when 1 then (select now from now) - 3
      when 0 then (select now from now) - 2
      else        (select now from now) - 1
    end as yesterday
), max_known_date as (
  select
    max(cast(date as date)) max_known_date
  from (
    select date, dataset, count(*)
    from dw.equities_fact
    where dataset <> 'ALPHA-VANTAGE'
      and ticker in ( select distinct ticker from dw.portfolio_dim where dataset = ( select datasource from datasource ) )
    group by
      1,2
    having count(*) > 30
   ) src
), beginning_of_year as (
  select date_trunc('year', ( select now from now)) + interval '1 day' beginning_of_year
), fx as (
  select 'GBP'::text as currency, 1.31 rate
), equities as (
  select
    ticker,
    date,
    avg(case when ticker = 'LON:FCH' then close * (select rate from fx where currency = 'GBP') / 100 else close end) as close
  from
    dw.equities_fact
  where
    date    = ( select today from date )
    or date = ( select yesterday from date )
    or date = ( select max_known_date from max_known_date )
    or date = ( select beginning_of_year from beginning_of_year )
    or date is null
  group by
    1,2
), portfolio as (
  select
    markets.description,
    portfolio.ticker,
    portfolio.quantity,
    portfolio.cost_per_share
  from
    dw.portfolio_dim portfolio
    join dw.markets_dim markets on markets.ticker = portfolio.ticker
  where
    portfolio.dataset = ( select datasource from datasource )
    and user = ( select _user from _user )
  group by
    1,2,3,4
), today as (
  select
    portfolio.description,
    equities.ticker,
    sum((quantity * cost_per_share))                 cost_basis,
    sum((quantity * coalesce(close,cost_per_share))) market_value,
    sum(((quantity * coalesce(close,cost_per_share)) - (quantity * cost_per_share))) gain_loss
  from
    equities
    right join portfolio on portfolio.ticker = equities.ticker
  where
    date = ( select today from date )
    or (case when equities.ticker in ('VMMXX')
              and date = (select beginning_of_year from beginning_of_year) then 1 else 0 end)
       = 1
  group by
    1,2
), yesterday as (
  select
    portfolio.description,
    equities.ticker,
    sum((quantity * coalesce(close,cost_per_share))) yesterday
  from
    equities
    right join portfolio on equities.ticker = portfolio.ticker
  where
    date in ( select yesterday from date )
  group by
    1,2
), ytd as (
  select
    portfolio.description,
    equities.ticker,
    sum((quantity * coalesce(close,cost_per_share))) market_value
  from
    equities
    right join portfolio on equities.ticker = portfolio.ticker
  where
    date = ( select beginning_of_year from beginning_of_year )
  group by
    1,2
), backup as (
  select
    portfolio.description,
    equities.ticker,
    sum((quantity * cost_per_share))                 cost_basis,
    sum((quantity * coalesce(close,cost_per_share))) market_value,
    sum(((quantity * coalesce(close,cost_per_share)) - (quantity * cost_per_share))) gain_loss
  from
    equities
    right join portfolio on equities.ticker = portfolio.ticker
  where
    date in ( select max_known_date from max_known_date )
    or (case when equities.ticker in ('VMMXX')
              and date = (select beginning_of_year from beginning_of_year) then 1 else 0 end)
       = 1
  group by
    1,2
), detail as (
  select
    coalesce(today.description, yesterday.description) description,
    coalesce(today.ticker,      yesterday.ticker) ticker,
    today.cost_basis, today.market_value, today.gain_loss,
    today.market_value - ytd.market_value ytd_gain_loss,
    today.market_value - yesterday.yesterday today_gain_loss
  from
    today
    full outer join yesterday on today.ticker = yesterday.ticker
    full outer join ytd on yesterday.ticker = ytd.ticker
  order by today.market_value desc
), detail_with_backup as (
  select
    coalesce(detail.description,    backup.description) description,
    coalesce(detail.ticker,         backup.ticker) ticker,
    coalesce(detail.cost_basis,     backup.cost_basis) cost_basis,
    coalesce(detail.market_value,   backup.market_value) market_value,
    coalesce(detail.gain_loss,      backup.gain_loss) gain_loss,
    coalesce(detail.ytd_gain_loss,  backup.market_value - ytd.market_value, 0) ytd_gain_loss,
    coalesce(detail.today_gain_loss, 0) today_gain_loss
  from
    detail
    full outer join backup on detail.description = backup.description
    full outer join ytd on backup.description = ytd.description
), summary as (
  select
    'Portfolio Total'::text description,
    'TOTAL'::text           ticker,
    sum(cost_basis)         cost_basis,
    sum(market_value)       market_value,
    sum(gain_loss)          gain_loss,
    sum(ytd_gain_loss)      ytd_gain_loss,
    sum(today_gain_loss)    today_gain_loss
  from
    detail_with_backup
), _union as (
  select * from summary
  union all
  select * from detail_with_backup where ticker <> ''
), report as (
  select
    ticker,
    (market_value / ( select market_value from summary ) * 100)::decimal(8,2) || '%' "mix_%",
    description,
    cost_basis::int ,
    market_value::int,
    today_gain_loss::int,
    (today_gain_loss / market_value * 100)::decimal(8,2) "today_gain_loss_%",
    ytd_gain_loss::int,
    (ytd_gain_loss / market_value * 100)::decimal(8,2)   "ytd_gain_loss_%",
    gain_loss::int total_gain_loss,
    (gain_loss / cost_basis * 100)::decimal(8,2)         "total_gain_loss_%"
  from
    _union
)
select * from report order by market_value desc
