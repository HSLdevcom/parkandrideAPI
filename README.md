# parkandrideAPI [![Build Status](https://travis-ci.org/HSLdevcom/parkandrideAPI.svg?branch=master)](https://travis-ci.org/HSLdevcom/parkandrideAPI) [![Coverage Status](https://coveralls.io/repos/HSLdevcom/parkandrideAPI/badge.png?branch=master)](https://coveralls.io/r/HSLdevcom/parkandrideAPI?branch=master)

HSL open source project to collect and share parking capacity information

System is described in procurement documents (in Finnish):
https://dl.dropboxusercontent.com/u/20567085/HSL_parkandride/LIIPI_Tarjouspyynt%C3%B6_20140527.zip

# Development

Install nginx and configure it as described in etc/nginx.conf. It routes /api requests to localhost:8080 and other (static resources) requests to
localhost:9000.

Start grunt watch in application/src/main/frontend (listens to localhost:9000).

Run fi.hsl.parkandride.Application (listens to localhost:8080). Application uses by default H2 in-memory DB. See below for Postresql.

# Profiles

## Environment profiles
Application is started with single environment profile. Any features required by the environment needs, are triggered in the environment profile by adding feature profiles via spring.profiles.include.

Profile|Description
-------|-----------
[env_local](https://github.com/HSLdevcom/parkandrideAPI/blob/master/application/src/main/resources/application-env_local.properties)       |development with H2
[env_local_psql](https://github.com/HSLdevcom/parkandrideAPI/blob/master/application/src/main/resources/application-env_local_psql.properties)  |development with postgres
[env_gocd](https://github.com/HSLdevcom/parkandrideAPI/blob/master/application/src/main/resources/application-env_gocd.properties)        |e2e testing in gocd environment 
[env_travis](https://github.com/HSLdevcom/parkandrideAPI/blob/master/application/src/main/resources/application-env_travis.properties)      |e2e testing in travis environment
[env_test](https://github.com/HSLdevcom/parkandrideAPI/blob/master/application/src/main/resources/application-env_test.properties)        |test deployment in aws
[env_demo](https://github.com/HSLdevcom/parkandrideAPI/blob/master/application/src/main/resources/application-env_demo.properties)        |demo deployment in aws

## Feature profiles

Profile|Description
-------|-----------
dev     |adds application/src/main/frontend/build as jetty resource
dev_api |brings up [TestController](https://github.com/HSLdevcom/parkandrideAPI/blob/master/application/src/main/java/fi/hsl/parkandride/dev/TestController.java)
psql    |postres is used instead of the default H2

# Postgresql

Postgresql can be activated with --spring.profiles.active=psql option for Application.

Schema and default test-user (liipi) can be installed on running Postgresql with etc/psql-init-db.sh. It passes all arguments to psql command. It requires an
Postgresql admin user to run.
