#!/bin/bash

set -x

PARKANDRIDE_VERSION=0.0.1-SNAPSHOT

AWS_USER=ubuntu
AWS_HOST=54.171.6.108

IDENTITY_FILE=/var/go/hsl-liipi.pem

# transfer
scp -r -i $IDENTITY_FILE etc $AWS_USER@$AWS_HOST:
scp -r -i $IDENTITY_FILE parkandride-application-$VERSION.jar $AWS_USER@$AWS_SERVER:parkandride-application-latest.jar

# stop and start docker container, build new images and restart containers
