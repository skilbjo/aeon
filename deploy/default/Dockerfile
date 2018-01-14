FROM gliderlabs/alpine:3.6

ENV JAVA_HOME="/usr/lib/jvm/java-1.8-openjdk" \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:MaxMetaspaceSize=128m -Duser.timezone=PST8PDT"




COPY target/uberjar/app.jar      /app.jar
COPY deploy                      /usr/local/deploy
COPY resources                   /usr/local/resources
COPY src                         /usr/local/src
COPY deploy/default/bashrc       /root/.bashrc
COPY deploy/default/vimrc        /root/.vimrc

RUN apk add -U openjdk8 \
    ca-certificates && update-ca-certificates && apk add -U openssl \
    bash bash-completion vim curl \
    tzdata coreutils && \
    cp /usr/share/zoneinfo/America/Los_Angeles /etc/localtime && \
    echo "America/Los_Angeles" >/etc/timezone
