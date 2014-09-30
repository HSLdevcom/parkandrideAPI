#!/bin/bash

# NOTE: this script is expectd to be run from project checkout dir

set -x

VERSION=`cat version`

AWS_TEST=ubuntu@54.171.6.108

IDENTITY_FILE=/var/go/hsl-liipi.pem

# transfer
function transfer() {
  APP_LATEST="parkandride-application-latest.jar"
  APP_NEW="parkandride-application-$VERSION.jar"

  scp -oStrictHostKeyChecking=no -r -i $IDENTITY_FILE etc $AWS_TEST:
  scp -r -i $IDENTITY_FILE staging/fi/hsl/parkandride/parkandride-application/$VERSION/$APP_NEW $AWS_TEST:$APP_NEW

  ssh -i $IDENTITY_FILE -t $AWS_TEST "rm $APP_LATEST && ln -s $APP_NEW $APP_LATEST"
}

function restart_container() {
  ssh -i $IDENTITY_FILE -t $AWS_TEST "sh etc/docker/app/restart.sh"
}

transfer
restart_container
