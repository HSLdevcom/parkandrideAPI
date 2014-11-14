#!/bin/bash

function init() {
  ROOT_DIR="$SCRIPT_DIR/../../../.."

  local v=`cat $ROOT_DIR/version`
  export APP_JAR="$ROOT_DIR/staging/fi/hsl/parkandride/parkandride-application/$v/parkandride-application-$v.jar"

  local protractor_dir="$ROOT_DIR/etc/protractor"
  cd $protractor_dir

  npm install
}

function cleanup() {
  set +e # don't fail on cleanup errors
  bash protractor.sh stop
  [ -n "$WITH_XVFB" ] && /etc/init.d/xvfb stop
}

function run() {
  trap cleanup EXIT

  [ -n "$WITH_XVFB" ] && /etc/init.d/xvfb start
  bash protractor.sh start
  bash protractor.sh wait_until_started
  bash protractor.sh test
  bash protractor.sh verify
}

VERBOSE="true"
source $(dirname $0)/../../main.inc
