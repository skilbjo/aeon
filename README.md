# compojure

[CircleCI Builds](https://circleci.com/gh/skilbjo/compojure)

[![CircleCI](https://circleci.com/gh/skilbjo/compojure/tree/master.svg?style=svg)](https://circleci.com/gh/skilbjo/compojure/tree/master)
[![quay.io](https://quay.io/repository/skilbjo/compojure/status "Docker Repository on Quay")](https://quay.io/repository/skilbjo/compojure)

## TODOs

- [X] Escape SQL injection (note: no decent sql injection escaping libaries out there); used `clojure.string/escape`
- [ ] Check if datasets conform to allowed map of datasets. Else, give a 404 instead of throwing an exception
- [ ] Seeing this error: `java.lang.OutOfMemoryError: Java heap space`

Note: formerly valid sql injection: `http://localhost:8080/api/equities'select%201'--/latest`

## API

    $ curl skilbjo.duckdns.org/api/equities/latest

    {"body":[{"open":148.45,"date":"2017-05-18T07:00:00Z","adj_volume":16101229.00,"adj_close":148.06, \
      "ticker":"FB","adj_low":147.96,"ex_dividend":0.00,"close":148.06,"volume":16101229.00,"high":149.39, \
      "adj_high":149.39,"split_ratio":1.00,"low":147.96,"adj_open":148.45,"dataset":"WIKI"}]}

## Deploy

Make sure the jks is in the prod servers:
    scp -P [port] java_key_store [user]@[host].:~

ssh to the prod servers, start tmux, and fire:
    ssh [alias].
    tm
    deploy/bin/run-docker

## Debugging

- Did the jks made it onto the host ok? (`cat ~/[jks]; ls -la ~/[jks]`)
- Did the jks made it into the container ok? (`cat /[jks]; ls -la /[jks]`)
- Web server Listening on the port? (via another shell in the container)?
- If in a VM, port forwarding in the VM? (`netstat -tulpen` or `sockstat -l 4`)
- Port forwarding on the host? (`VBoxManage controlvm "default" natpf1 "tcp-port8443,tcp,,8443,,8443";`
- Latest docker image! (key!)

Did it work? (locally)
    curl localhost:8080
    curl --insecure localhost:8443

Did it work? (externally)
    curl $(curl v4.ifconfig.co 2>/dev/null)
    curl "https://$(curl v4.ifconfig.co 2>/dev/null)"

## Set up on AWS

    sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-ports 8080
    sudo iptables -t nat -A PREROUTING -p tcp --dport 443 -j REDIRECT --to-ports 8443

Revert

    sudo iptables -t nat -D PREROUTING 1

## Set up on FreeBSD (docker-machine / VirtualBox)

Need to portforward host to guest ports.

    ssh docker@$(docker-machine ip default) -L 8443:localhost:8443

or

    VBoxManage controlvm "default" natpf1 "tcp-port8080,tcp,,8080,,8080";
    VBoxManage controlvm "default" natpf1 "tcp-port8443,tcp,,8443,,8443";

## Git remotes

    $ git remote add pi-vpn ssh://skilbjo@router.:43/~/deploy/git/compojure.git
    $ git remote add pi-home ssh://skilbjo@pi1/~/deploy/git/compojure.git

## Resources
- [ ] [http://markgandolfo.com/blog/2014/01/10/a-simple-blog-in-clojure/](http://markgandolfo.com/blog/2014/01/10/a-simple-blog-in-clojure/)
