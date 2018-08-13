select
  user as _user,
  password
from
  aeon.users
where
  user     = ':user' and
  password = ':password'
limit 1
