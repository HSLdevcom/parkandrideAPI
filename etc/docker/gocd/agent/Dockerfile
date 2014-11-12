FROM dockerfile/java:oracle-java8
MAINTAINER HSLdevcom/parkandrideAPI "https://github.com/HSLdevcom/parkandrideAPI"
ENV REFRESHED_AT 2014-11-12

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

# Install Maven
ENV M2_HOME /usr/local/apache-maven-3.2.3
RUN wget -nv -O ${M2_HOME}.tar.gz http://mirror.netinch.com/pub/apache/maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz
RUN tar xzvf ${M2_HOME}.tar.gz -C /usr/local > /dev/null
ENV MAVEN_OPTS -Xms256m -Xmx512m
ENV PATH $M2_HOME/bin:$PATH

# Install Xvfb (adapted from https://github.com/exratione/protractor-selenium-server-cookbook)
# Xvfb is a headless X server that will allow Firefox, Chromium, etc, to run.
# Note that there may be various dependencies that still need to be installed
# on a server, such as font support.
RUN apt-get install -qqy xvfb x11-xkb-utils xfonts-cyrillic xfonts-100dpi xfonts-75dpi xfonts-base xfonts-scalable xserver-xorg-core
# Optional but nifty,  For capturing screenshots of Xvfb display:
RUN apt-get install -qqy imagemagick x11-apps

# Install Browsers
# The dbus-x11 package prevents some errors from occurring when using Firefox.
RUN apt-get install -qqy dbus-x11 firefox phantomjs
# On a headless Ubuntu box there are some items needed by PhantomJS that
# are not present. PhantomJS will silently fail if they aren't there - which is
# a real pain to deal with. This is the crucial item:
RUN apt-get install -qqy libfontconfig1-dev

# Install Xvfb init script
ENV DISPLAY :99
ADD etc-init.d-xvfb /etc/init.d/xvfb
RUN chmod a+x /etc/init.d/xvfb
RUN chmod u+s `which Xvfb`

ADD hsl-liipi.pem /var/go/
RUN mkdir -p /var/go/.m2
ADD m2.settings /var/go/.m2/settings.xml
ADD npmrc /var/go/.npmrc

RUN chown -R go:go /var/go
ENV HOME /var/go
USER go

CMD /etc/init.d/go-agent start
