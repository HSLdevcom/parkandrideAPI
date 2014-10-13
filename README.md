parkandrideAPI [![Build Status](https://travis-ci.org/HSLdevcom/parkandrideAPI.svg)](https://travis-ci.org/HSLdevcom/parkandrideAPI) [![Coverage Status](https://coveralls.io/repos/HSLdevcom/parkandrideAPI/badge.png?branch=master)](https://coveralls.io/r/HSLdevcom/parkandrideAPI?branch=master)
==============

HSL open source project to collect and share parking capacity information

System is described in procurement documents (in Finnish):
https://dl.dropboxusercontent.com/u/20567085/HSL_parkandride/LIIPI_Tarjouspyynt%C3%B6_20140527.zip


Development
===========

Install nginx and configure it as described in etc/nginx.conf. It routes /api requests to localhost:8080 and other (static resources) requests to
localhost:9000.

Start grunt watch in application/src/main/frontend (listens to localhost:9000).

Run fi.hsl.parkandride.Application (listens to localhost:8080). Application uses by default H2 in-memory DB. See below for Postresql.


Postgresql
==========

Postgresql can be activated with --spring.profiles.active=psql option for Application.

Schema and default test-user (liipi) can be installed on running Postgresql with etc/psql-init-db.sh. It passes all arguments to psql command. It requires an
Postgresql admin user to run.
