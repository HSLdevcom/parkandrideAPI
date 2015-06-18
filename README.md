# parkandrideAPI [![Build Status](https://travis-ci.org/HSLdevcom/parkandrideAPI.svg?branch=master)](https://travis-ci.org/HSLdevcom/parkandrideAPI) [![Coverage Status](https://coveralls.io/repos/HSLdevcom/parkandrideAPI/badge.png?branch=master)](https://coveralls.io/r/HSLdevcom/parkandrideAPI?branch=master)

HSL open source project to collect and share parking capacity information. 

The service can be found at https://p.hsl.fi/

API documentation can be found at https://p.hsl.fi/docs/

# Development

In both the approaches described below, the application runs in `localhost:8080` after the steps are completed.

### Without livereload
> Browser refresh is required when frontend resources are updated.

* run `grunt watch` in application/src/main/frontend
* run application with env_local profile. Application can be run
  * from an IDE by running [fi.hsl.parkandride.Application](https://github.com/HSLdevcom/parkandrideAPI/blob/master/application/src/main/java/fi/hsl/parkandride/Application.java) 
  * from command line by running the packaged jar (non-frontend changes require restart) 
     * `java -jar application/target/parkandride-application-1-SNAPSHOT.jar --spring.profiles.active=env_local`

### With livereload
* do the steps defined in 'Without livereload' with the distinction that application is started at port 9100, e.g.
 * `java -jar application/target/parkandride-application-1-SNAPSHOT.jar --server.port=9100 --spring.profiles.active=env_local`
* install nginx and configure it as described in [etc/nginx.conf](https://github.com/HSLdevcom/parkandrideAPI/blob/master/etc/nginx.conf). It routes
  * /api requests to localhost:9100
  * other requests to localhost:9000 (grunt-connect listens at this port)

# Profiles

## Environment profiles
Application is started with **a single environment profile**. Any features required by the environment, are triggered in the environment profile by adding **feature profiles** under property `spring.profiles.include`.

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
dev     |adds application/src/main/frontend/build as webapp resource
dev_api |brings up [DevController](https://github.com/HSLdevcom/parkandrideAPI/blob/master/application/src/main/java/fi/hsl/parkandride/dev/DevController.java)
psql    |postgres is used instead of the default H2

# Postgresql initialization

Schema and default test-user (liipi) can be installed on running Postgresql with [etc/scripts/db/psql-init-db.sh](https://github.com/HSLdevcom/parkandrideAPI/blob/master/etc/scripts/db/psql-init-db.sh). See the file for example usage.

# License

Copyright © 2015 [HSL](https://www.hsl.fi/)

The source code of this program is dual-licensed under the [EUPL v1.2](LICENSE-EUPL.txt) and [AGPLv3](LICENSE-AGPL.txt) licenses.

The data hosted in this service is licensed under the [CC BY 4.0](http://creativecommons.org/licenses/by/4.0/) license.
