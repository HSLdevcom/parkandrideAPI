#!/bin/bash

# NOTE: this script is expectd to be run from project checkout dir

set -x

VERSION=0.0.1-SNAPSHOT

AWS_USER=ubuntu
AWS_HOST=54.171.6.108

IDENTITY_FILE=/var/go/hsl-liipi.pem

# transfer
scp -oStrictHostKeyChecking=no -r -i $IDENTITY_FILE etc $AWS_USER@$AWS_HOST:
scp -r -i $IDENTITY_FILE parkandride-application-$VERSION.jar $AWS_USER@$AWS_HOST:parkandride-application-latest.jar

# stop and start docker container
ssh -i $IDENTITY_FILE -t "$AWS_USER@$AWS_HOST" "etc/deploy/restart.sh"