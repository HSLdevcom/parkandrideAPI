#!/bin/bash

function init() {
  ROOT_DIR="$SCRIPT_DIR/../../../.."

  local v=`cat $ROOT_DIR/version`
  export APP_JAR="$ROOT_DIR/staging/fi/hsl/parkandride/parkandride-application/$v/parkandride-application-$v.jar"

  local protractor_dir="$ROOT_DIR/etc/protractor"
  cd $protractor_dir

  npm install

  export SPRING_PROFILES_ACTIVE=env_gocd
}

function cleanup() {
  set +e # don't fail on cleanup errors
  bash protractor.sh stop
}

function init_db {
  LIIPI_DB=liipici bash $ROOT_DIR/etc/scripts/db/psql-init-db.sh -h dev.cvokarbgtqbl.eu-west-1.rds.amazonaws.com -U devmaster postgres
}

function run() {
  trap cleanup EXIT

  [ "$INIT_DB" = "yes" ] && init_db
  bash protractor.sh start
  bash protractor.sh wait_until_started
  bash protractor.sh test
  bash protractor.sh verify
}

VERBOSE="true"
source $(dirname $0)/../../main.inc
