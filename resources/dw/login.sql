select
  username,
  password
from
  aeon.users
where
  username = ':user' and
  password = ':password'
limit 1
