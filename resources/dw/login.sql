select
  _user,
  password
from
  aeon.users
where
  _user    = ':user' and
  password = ':password'
limit 1
