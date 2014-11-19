#!/bin/bash

function init() {
  ROOT_DIR="$SCRIPT_DIR/../../../.."

  local v=`cat $ROOT_DIR/version`
  export APP_JAR="$ROOT_DIR/staging/fi/hsl/parkandride/parkandride-application/$v/parkandride-application-$v.jar"

  local protractor_dir="$ROOT_DIR/etc/protractor"
  cd $protractor_dir

  npm install

  export SPRING_PROFILES_ACTIVE=psql,e2e
  export SPRING_DATASOURCE_URL=jdbc:postgresql_postGIS://dev.cvokarbgtqbl.eu-west-1.rds.amazonaws.com:5432/liipi?searchpath=liipi
}

function cleanup() {
  set +e # don't fail on cleanup errors
  bash protractor.sh stop
}

function run() {
  trap cleanup EXIT

  bash protractor.sh start
  bash protractor.sh wait_until_started
  bash protractor.sh test
  bash protractor.sh verify
}

VERBOSE="true"
source $(dirname $0)/../../main.inc
