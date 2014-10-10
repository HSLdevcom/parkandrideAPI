FROM ubuntu:12.04
MAINTAINER HSLdevcom/parkandrideAPI "https://github.com/HSLdevcom/parkandrideAPI"
ENV REFRESHED_AT 2014-10-09

# Install Xvfb
# Xvfb is a headless X server that will allow Firefox, Chromium, etc, to run.
# Note that there may be various dependencies that still need to be installed
# on a server, such as font support.
# Possible additional dependencies
# - gtk2-engines-pixbuf
# Optional but nifty: For capturing screenshots of Xvfb display:
# - imagemagick
# - x11-apps
RUN \
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update && \
  apt-get -y upgrade && \
  apt-get install -qqy xvfb
RUN apt-get install -qqy x11-xkb-utils xfonts-100dpi xfonts-75dpi xfonts-base xfonts-scalable xserver-xorg-core

# Install Browsers
# The dbus-x11 package prevents some errors from occurring when using Firefox.
RUN apt-get install -qqy dbus-x11 firefox chromium-browser phantomjs

# On a headless Ubuntu box there are some items needed by PhantomJS that
# are not present. PhantomJS will silently fail if they aren't there - which is
# a real pain to deal with. This is the crucial item:
RUN apt-get install -qqy libfontconfig1-dev

# Install common
RUN \
  apt-get install -y build-essential && \
  apt-get install -y software-properties-common && \
  apt-get install -y python-software-properties && \
  apt-get install -y byobu curl git htop man unzip vim wget && \
  rm -rf /var/lib/apt/lists/*

# Install Java
RUN \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

# Install Go
RUN wget -nv -O /tmp/go-agent.deb http://download.go.cd/gocd-deb/go-agent-14.2.0-377.deb
RUN dpkg -i /tmp/go-agent.deb
RUN rm /tmp/go-agent.deb
RUN sed -r -i "s/^(GO_SERVER)=(.*)/\1=\$GOSERVER_PORT_8153_TCP_ADDR/g" /etc/default/go-agent
RUN sed -r -i "s/^(DAEMON)=(.*)/\1=N/g" /etc/default/go-agent

# Install maven
ENV M2_HOME /usr/local/apache-maven-3.2.3
RUN wget -nv -O ${M2_HOME}.tar.gz http://mirror.netinch.com/pub/apache/maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz
RUN tar xzvf ${M2_HOME}.tar.gz -C /usr/local > /dev/null
ENV MAVEN_OPTS -Xms256m -Xmx512m
ENV PATH $M2_HOME/bin:$PATH

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
