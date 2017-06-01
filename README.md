# compojure

[CircleCI Builds](https://circleci.com/gh/skilbjo/compojure)

[![CircleCI](https://circleci.com/gh/skilbjo/compojure/tree/master.svg?style=svg)](https://circleci.com/gh/skilbjo/compojure/tree/master)
[![quay.io](https://quay.io/repository/skilbjo/compojure/status "Docker Repository on Quay")](https://quay.io/repository/skilbjo/compojure)

## TODOs

- [X] Escape SQL injection (note: no decent sql injection escaping libaries out there); used `clojure.string/escape`
- [ ] Check if datasets conform to allowed map of datasets. Else, give a 404 instead of throwing an exception

## API

    $ curl skilbjo.duckdns.org/api/equities/latest

    {"body":[{"open":148.45,"date":"2017-05-18T07:00:00Z","adj_volume":16101229.00,"adj_close":148.06, \
      "ticker":"FB","adj_low":147.96,"ex_dividend":0.00,"close":148.06,"volume":16101229.00,"high":149.39, \
      "adj_high":149.39,"split_ratio":1.00,"low":147.96,"adj_open":148.45,"dataset":"WIKI"}]}

## Git remotes

    $ git remote add pi-vpn ssh://skilbjo@router.:43/~/deploy/git/compojure.git
    $ git remote add pi-home ssh://skilbjo@pi1/~/deploy/git/compojure.git

## Resources
- [ ] [http://markgandolfo.com/blog/2014/01/10/a-simple-blog-in-clojure/](http://markgandolfo.com/blog/2014/01/10/a-simple-blog-in-clojure/)
