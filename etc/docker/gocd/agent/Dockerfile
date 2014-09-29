FROM dockerfile/java:oracle-java8
MAINTAINER HSLdevcom/parkandrideAPI "https://github.com/HSLdevcom/parkandrideAPI"
ENV REFRESHED_AT 2014-09-29

RUN wget -nv -O /tmp/go-agent.deb http://download.go.cd/gocd-deb/go-agent-14.2.0-377.deb
RUN dpkg -i /tmp/go-agent.deb
RUN rm /tmp/go-agent.deb
RUN sed -r -i "s/^(GO_SERVER)=(.*)/\1=\$GOSERVER_PORT_8153_TCP_ADDR/g" /etc/default/go-agent
RUN sed -r -i "s/^(DAEMON)=(.*)/\1=N/g" /etc/default/go-agent

ENV M2_HOME /usr/local/apache-maven-3.2.3
RUN wget -nv -O ${M2_HOME}.tar.gz http://mirror.netinch.com/pub/apache/maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz
RUN tar xzvf ${M2_HOME}.tar.gz -C /usr/local > /dev/null
ENV MAVEN_OPTS -Xms256m -Xmx512m
ENV PATH $M2_HOME/bin:$PATH

RUN apt-get -yqq update > /dev/null
RUN apt-get -yqq install phantomjs > /dev/null

VOLUME ["/var/lib/go/agent"]

ENV HOME /var/go
WORKDIR /var/go
USER go
ADD ./hsl-liipi.pem .

CMD /etc/init.d/go-agent start