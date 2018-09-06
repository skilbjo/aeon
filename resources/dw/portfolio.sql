with now as (
  select (now() at time zone 'pst')::date now
), _user as (
  select ':user'::text as _user
), date as (
  select
    (select now from now) today,
    case (extract(isodow from (select now from now))::integer) % 7
      when 1 then (select now from now) - 3
      when 0 then (select now from now) - 2
      else        (select now from now) - 1
    end as yesterday
), max_known_date as (
  select max(date) max_known_date from ( select date, count(*) from dw.equities_fact group by date having count(*) > 40) src
), beginning_of_year as (
  select date_trunc('year', ( select now from now)) + interval '1 day' beginning_of_year
), today as (
  select
    markets.description,
    markets.ticker,
    sum((quantity * cost_per_share))                 cost_basis,
    sum((quantity * coalesce(close,cost_per_share))) market_value,
    sum(((quantity * coalesce(close,cost_per_share)) - (quantity * cost_per_share))) gain_loss
  from
    dw.equities_fact equities
    right join dw.portfolio_dim portfolio on equities.dataset = portfolio.dataset
                                          and equities.ticker = portfolio.ticker
                                          and _user = ( select _user from _user )
    join dw.markets_dim markets on portfolio.dataset = markets.dataset and portfolio.ticker = markets.ticker
  where
    date in ( select today from date )
    or (case when markets.ticker in ('VGWAX') and date is null then 1 else 0 end)
       = 1
    or (case when markets.ticker in ('VMMXX') and date in (select yesterday from date) then 1 else 0 end)
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
    dw.equities_fact equities
    right join dw.portfolio_dim portfolio on equities.dataset = portfolio.dataset
                                          and equities.ticker = portfolio.ticker
                                          and _user = ( select _user from _user )
    join dw.markets_dim markets on portfolio.dataset = markets.dataset and portfolio.ticker = markets.ticker
  where
    date in ( select yesterday from date ) or date is null
  group by
    1,2
), ytd as (
  select
    markets.description,
    markets.ticker,
    sum((quantity * coalesce(close,cost_per_share))) market_value
  from
    dw.equities_fact equities
    right join dw.portfolio_dim portfolio on equities.dataset = portfolio.dataset and equities.ticker = portfolio.ticker
    join dw.markets_dim markets on portfolio.dataset = markets.dataset and portfolio.ticker = markets.ticker
  where
    date = ( select beginning_of_year from beginning_of_year )
  group by
    1,2
), backup as (
  select
    markets.description,
    markets.ticker,
    sum((quantity * cost_per_share))                 cost_basis,
    sum((quantity * coalesce(close,cost_per_share))) market_value,
    sum(((quantity * coalesce(close,cost_per_share)) - (quantity * cost_per_share))) gain_loss
  from
    dw.equities_fact equities
    right join dw.portfolio_dim portfolio on equities.dataset = portfolio.dataset
                                          and equities.ticker = portfolio.ticker
                                          and _user = ( select _user from _user )
    join dw.markets_dim markets on portfolio.dataset = markets.dataset and portfolio.ticker = markets.ticker
  where
    date in ( select max_known_date from max_known_date )
    or (case when markets.ticker in ('VGWAX') and date is null then 1 else 0 end)
       = 1
  group by
    1,2
), detail as (
  select
    coalesce(today.description,yesterday.description) description,
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
    coalesce(detail.description,  backup.description) description,
    coalesce(detail.cost_basis,   backup.cost_basis) cost_basis,
    coalesce(detail.market_value, backup.market_value) market_value,
    coalesce(detail.gain_loss,    backup.gain_loss) gain_loss,
    coalesce(detail.ytd_gain_loss, backup.market_value - ytd.market_value, 0) ytd_gain_loss,
    coalesce(detail.today_gain_loss, 0) today_gain_loss
  from
    detail
    full outer join backup on detail.description = backup.description
    full outer join ytd on backup.description = ytd.description
), summary as (
  select
    'Portfolio Total'::text description,
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
  select * from detail_with_backup
), report as (
  select
    description, cost_basis::int , market_value::int,
    today_gain_loss::int,
    (today_gain_loss / market_value * 100)::decimal(8,2) "today_gain_loss_%",
    ytd_gain_loss::int,
    (ytd_gain_loss / market_value * 100)::decimal(8,2)   "ytd_gain_loss_%",
    gain_loss::int total_gain_loss,
    (gain_loss / cost_basis * 100)::decimal(8,2)         "total_gain_loss_%"
  from
    _union
)
select * from report
