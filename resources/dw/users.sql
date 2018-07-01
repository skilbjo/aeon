select
  username,
  password,
from
  aoin.users
where
  username     = ':user'
  and password = ':password'
limit 1
