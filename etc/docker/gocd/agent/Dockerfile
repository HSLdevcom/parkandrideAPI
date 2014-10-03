FROM dockerfile/java:oracle-java8
MAINTAINER HSLdevcom/parkandrideAPI "https://github.com/HSLdevcom/parkandrideAPI"
ENV REFRESHED_AT 2014-09-29

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

RUN apt-get -yqq update > /dev/null

# Install phantomjs
RUN apt-get -yqq install phantomjs > /dev/null

# jpetazzo/dind: basic stuff
RUN apt-get install -qqy iptables ca-certificates lxc

# jpetazzo/dind: install Docker from Docker Inc. repositories.
RUN apt-get install -qqy apt-transport-https
RUN echo deb https://get.docker.io/ubuntu docker main > /etc/apt/sources.list.d/docker.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 36A1D7869245C8950F966E92D8576A8BA88D21E9
RUN apt-get update -qq
RUN apt-get install -qqy lxc-docker

# jpetazzo/dind: install the magic wrapper.
ADD ./wrapdocker /usr/local/bin/wrapdocker
RUN chmod +x /usr/local/bin/wrapdocker

# jpetazzo/dind: define additional metadata for our image.
VOLUME /var/lib/docker

VOLUME ["/var/lib/go/agent"]

ENV HOME /var/go
USER go

ADD hsl-liipi.pem /var/go/
RUN mkdir -p /var/go/.m2
ADD m2.settings /var/go/.m2/settings.xml

USER root
ADD dockergoagent.sh /usr/local/bin/dockergoagent.sh
RUN chmod +x /usr/local/bin/dockergoagent.sh
CMD /usr/local/bin/dockergoagent.sh