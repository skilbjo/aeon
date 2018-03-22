FROM gliderlabs/alpine:3.5

ENV JAVA_HOME="/usr/lib/jvm/java-1.8-openjdk" \
    JAVA_OPTS="-Duser.timezone=UTC -Xms256m -Xmx1g -XX:MaxMetaspaceSize=512m -XX:-HeapDumpOnOutOfMemoryError" \
    PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/bin:/usr/lib/jvm/java-1.8-openjdk/bin" \
    CMD="usr/local/deploy/bin/run-job"




COPY target/uberjar/app.jar      /app.jar
COPY deploy                      /usr/local/deploy
COPY resources                   /usr/local/resources
COPY src                         /usr/local/src
COPY deploy/default/bashrc       /root/.bashrc
COPY deploy/default/vimrc        /root/.vimrc

RUN apk add -U openjdk8 bash bash-completion coreutils tzdata vim git \
    && mkdir -p /root/.vim  && git clone https://github.com/Shougo/neobundle.vim ~/.vim/bundle && ~/.vim/bundle/bin/install.sh \
    && cp /usr/share/zoneinfo/America/Los_Angeles /etc/localtime \
    && echo "America/Los_Angeles" >/etc/timezone \
    && rm -rf /var/cache/apk/*
