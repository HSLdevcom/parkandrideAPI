FROM dockerfile/java:oracle-java8
MAINTAINER HSLdevcom/parkandrideAPI "https://github.com/HSLdevcom/parkandrideAPI"
ENV REFRESHED_AT 2014-10-03

# Docker-in-Docker
RUN apt-get update -qq
RUN apt-get install -qqy iptables ca-certificates lxc
RUN apt-get install -qqy apt-transport-https
RUN echo deb https://get.docker.io/ubuntu docker main > /etc/apt/sources.list.d/docker.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 36A1D7869245C8950F966E92D8576A8BA88D21E9
RUN apt-get update -qq
RUN apt-get install -qqy lxc-docker
VOLUME /var/lib/docker
ADD etc-sudoers.d-docker /etc/sudoers.d/docker

# Install Go
RUN wget -nv -O /tmp/go-agent.deb http://download.go.cd/gocd-deb/go-agent-14.2.0-377.deb
RUN dpkg -i /tmp/go-agent.deb
RUN rm /tmp/go-agent.deb
RUN sed -r -i "s/^(GO_SERVER)=(.*)/\1=\$GOSERVER_PORT_8153_TCP_ADDR/g" /etc/default/go-agent
RUN sed -r -i "s/^(DAEMON)=(.*)/\1=N/g" /etc/default/go-agent
RUN gpasswd -a go docker

# Install maven
ENV M2_HOME /usr/local/apache-maven-3.2.3
RUN wget -nv -O ${M2_HOME}.tar.gz http://mirror.netinch.com/pub/apache/maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz
RUN tar xzvf ${M2_HOME}.tar.gz -C /usr/local > /dev/null
ENV MAVEN_OPTS -Xms256m -Xmx512m
ENV PATH $M2_HOME/bin:$PATH

# Install phantomjs
RUN apt-get -qqy install phantomjs > /dev/null

# Install Google Chrome
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
RUN echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list
RUN apt-get -qqy update
RUN apt-get -qqy install libxpm4 libxrender1 libgtk2.0-0 libnss3 libgconf-2-4
RUN apt-get -qqy install google-chrome-stable

# Dependencies to make "headless" chrome/selenium work
RUN apt-get -qqy install xvfb gtk2-engines-pixbuf
RUN apt-get -qqy install xfonts-cyrillic xfonts-100dpi xfonts-75dpi xfonts-base xfonts-scalable

# Optional but nifty: For capturing screenshots of Xvfb display:
RUN apt-get -qqy install imagemagick x11-apps

# Install Xvfb init script
ENV DISPLAY :99
ADD etc-init.d-xvfb /etc/init.d/xvfb
RUN chmod a+x /etc/init.d/xvfb
RUN chmod u+s `which Xvfb`

ADD hsl-liipi.pem /var/go/
RUN mkdir -p /var/go/.m2
ADD m2.settings /var/go/.m2/settings.xml

RUN chown -R go:go /var/go
ENV HOME /var/go
#RUN adduser go sudo
#RUN echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers
USER go

CMD /etc/init.d/go-agent start
