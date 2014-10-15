#!/bin/bash

# NOTE: this script is expectd to be run from project checkout dir

set -eu
: ${TARGET_ENV:?}
: ${TARGET_ENV_PORT:?}
set -x

VERSION=`cat version`
AWS_TEST=ubuntu@54.171.6.108
IDENTITY_FILE=/var/go/hsl-liipi.pem

DOCKER_REGISTRY="172.31.0.27:5000"
LOCAL_IMAGE="parkandrideapi/server:$TARGET_ENV"
REGISTRY_IMAGE="$DOCKER_REGISTRY/$LOCAL_IMAGE"
CONTAINER_NAME="parkandrideapi-server-$TARGET_ENV"
DOCKERFILE_DIR="etc/docker/app"
APP_LATEST="parkandride-application-latest.jar"
APP_NEW="parkandride-application-$VERSION.jar"

function build_image() {
  cp staging/fi/hsl/parkandride/parkandride-application/$VERSION/$APP_NEW $DOCKERFILE_DIR/$APP_LATEST
  cd $DOCKERFILE_DIR
  docker build -t $LOCAL_IMAGE .
}

function push_image() {
  docker tag $LOCAL_IMAGE $REGISTRY_IMAGE
  docker push $REGISTRY_IMAGE
}

function restart_container() {
  ssh -i $IDENTITY_FILE -oStrictHostKeyChecking=no $AWS_TEST "docker stop $CONTAINER_NAME"
  ssh -i $IDENTITY_FILE $AWS_TEST "docker rm $CONTAINER_NAME"
  ssh -i $IDENTITY_FILE $AWS_TEST "docker pull $REGISTRY_IMAGE"
  ssh -i $IDENTITY_FILE $AWS_TEST "docker run -e SPRING_PROFILES_ACTIVE=e2e -d -p $TARGET_ENV_PORT:8080 --name $CONTAINER_NAME $REGISTRY_IMAGE"
}

build_image
push_image
restart_container
