# Liipi - Park and Ride API [![Build Status](https://travis-ci.org/HSLdevcom/parkandrideAPI.svg?branch=master)](https://travis-ci.org/HSLdevcom/parkandrideAPI) [![Coverage Status](https://coveralls.io/repos/HSLdevcom/parkandrideAPI/badge.png?branch=master)](https://coveralls.io/r/HSLdevcom/parkandrideAPI?branch=master)

HSL open source project to collect and share parking capacity information. 

The service can be found at <https://p.hsl.fi>

API documentation can be found at <https://p.hsl.fi/docs>


## Development

To build the project, you will need [Docker](https://www.docker.com/community-edition). For development outside Docker, you will also need [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [Maven](https://maven.apache.org/), [Node.js](https://nodejs.org/) and [Yarn](https://yarnpkg.com/). 

Building the project

    docker-compose build

Starting the application

    docker-compose up -d db api web

The web application will run on <http://localhost:8080> and the PostgreSQL database on `localhost:5432`

Running UI tests

    docker-compose run --rm test

Tagging the temporary Docker containers which were used for building the app, so that the dependency cache will survive `docker image prune`  

    ./cache-builder-images.sh 

Running tests against PostgreSQL (after caching builder images)

    docker-compose up -d db
    docker run --rm -v "$PWD:/project" --network host liipi-api-builder-cache mvn clean verify -P psql


### Developing locally, outside Docker

Build the API backend

    mvn clean verify

The test output will be saved in `target/surefire-reports`

Run tests against PostgreSQL (default is H2)

    mvn clean verify -P psql

Build the web frontend

    cd web
    yarn install
    yarn run build


### Running UI tests locally

Preparations

    cd test
    yarn install
    yarn run webdriver-update
    yarn run webdriver-start

Running tests

    yarn run test

Running only some tests

    yarn run test --specs specs/auth/auth.spec.js


# Deployment

Publish the latest built images as version 123

    ./publish.sh 123


# License

Copyright © 2015-2018 [HSL](https://www.hsl.fi/)

The source code of this program is dual-licensed under the [EUPL v1.2](LICENSE-EUPL.txt) and [AGPLv3](LICENSE-AGPL.txt) licenses.

The data hosted in this service is licensed under the [CC BY 4.0](http://creativecommons.org/licenses/by/4.0/) license.
