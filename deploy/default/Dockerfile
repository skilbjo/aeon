FROM gliderlabs/alpine:latest

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk8

COPY __UBERJAR___                   /app.jar

RUN apk add -U && \
    openjdk8
    bash bash-completion curl vim && \
    tzdata coreutils && \
    cp /usr/share/zoneinfo/America/Los_Angeles /etc/localtime && \
    echo "America/Los_Angeles" > /etc/timezone && \
    rm -rf /var/cache/apk/*

COPY deploy                         /usr/local/deploy
COPY deploy/default/bashrc          /root/.bashrc
#COPY deploy/default/vimrc           /root/.vimrc

RUN mkdir -p ~/.vim/bundle && \
    git clone https://github.com/Shougo/neobundle.vim ~/.vim/bundle && \
    curl https://raw.githubusercontent.com/skilbjo/dotfiles/master/vim/vimrc > ~/.vimrc && \
    curl https://raw.githubusercontent.com/skilbjo/dotfiles/master/vim/plugins.vim > ~/.vim/plugins.vim && \
    curl https://raw.githubusercontent.com/skilbjo/dotfiles/master/vim/plugin_settings.vim > ~/.vim/plugin_settings.vim && \
    curl https://raw.githubusercontent.com/skilbjo/dotfiles/master/vim/settings.vim > ~/.vim/settings.vim && \
    curl https://raw.githubusercontent.com/skilbjo/dotfiles/master/vim/keybindings.vim > ~/.vim/keybindings.vim && \
    curl https://raw.githubusercontent.com/skilbjo/dotfiles/master/vim/functions.vim > ~/.vim/functions.vim && \
    ~/.vim/bundle/bin/install.sh && \
    ~/.vim/bundle/bin/neoinstall
