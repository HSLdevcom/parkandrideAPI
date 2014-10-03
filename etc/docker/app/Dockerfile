FROM dockerfile/java:oracle-java8
MAINTAINER HSLdevcom/parkandrideAPI "https://github.com/HSLdevcom/parkandrideAPI"
ENV REFRESHED_AT 2014-09-26

EXPOSE 8080
ADD parkandride-application-latest.jar /data/parkandride/parkandride-application-latest.jar
CMD java -jar /data/parkandride/parkandride-application-latest.jar