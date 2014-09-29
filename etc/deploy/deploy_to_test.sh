#!/bin/bash

set -x

VERSION=0.0.1-SNAPSHOT

AWS_USER=ubuntu
AWS_HOST=54.171.6.108

IDENTITY_FILE=/var/go/hsl-liipi.pem
SCRIPT_DIR=`dirname $0`
ETC_DIR=$SCRIPT_DIR/..

# transfer
scp -oStrictHostKeyChecking=no -r -i $IDENTITY_FILE $ETC_DIR $AWS_USER@$AWS_HOST:
scp -r -i $IDENTITY_FILE parkandride-application-$VERSION.jar $AWS_USER@$AWS_HOST:parkandride-application-latest.jar

# stop and start docker container
ssh -i $IDENTITY_FILE -t "$AWS_USER@$AWS_SERVER" "$SCRIPT_DIR/restart.sh"