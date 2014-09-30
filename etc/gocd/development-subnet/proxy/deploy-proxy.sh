#!/bin/bash

set -x

AWS_CI=ubuntu@54.194.155.174

IDENTITY_FILE=/var/go/hsl-liipi.pem

function version() {
  shopt -s nullglob
  proxy_jar=(`dirname $0`/*.jar)
  v=`echo $proxy_jar | sed -e 's/.*-\(.*\)\.jar/\1/'`
  echo $v
}
VERSION=`version`

function transfer() {
  PROXY_LATEST="proxy-latest.jar"
  PROXY_NEW="proxy-$VERSION.jar"

  scp -oStrictHostKeyChecking=no -r -i $IDENTITY_FILE etc/docker/proxy $AWS_CI:
  scp -r -i $IDENTITY_FILE `dirname $0`/$PROXY_NEW $AWS_CI:proxy/$PROXY_NEW

  ssh -i $IDENTITY_FILE -t $AWS_CI "cd proxy && rm $PROXY_LATEST && ln -s $PROXY_NEW $PROXY_LATEST"
}
transfer