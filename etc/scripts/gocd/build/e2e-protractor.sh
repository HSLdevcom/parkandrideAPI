#!/bin/bash

function init() {
  local v=`cat version`
  local protractor_dir="etc/protractor"
  cd $protractor_dir

  export APP_JAR="../../staging/fi/hsl/parkandride/parkandride-application/$v/parkandride-application-$v.jar"
}

function run() {
  [ -n "$WITH_XVFB" ] && /etc/init.d/xvfb start
  bash protractor.sh start
  bash protractor.sh wait_until_started
  bash protractor.sh test
  bash protractor.sh stop
  [ -n "$WITH_XVFB" ] && /etc/init.d/xvfb stop
  bash protractor.sh verify
}

VERBOSE="true"
source $(dirname $0)/../../main.inc
