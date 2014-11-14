#!/bin/bash

function init() {
  JARFile=${APP_JAR:-"$SCRIPT_DIR/../../application/target/parkandride-application-0.0.1-SNAPSHOT.jar"}
  PIDFile="$SCRIPT_DIR/application.pid"
  LOGFile="$SCRIPT_DIR/application.log"
  NODE_MODULES="$SCRIPT_DIR/node_modules"
  : ${SERVER_URL:=http://localhost:8080}

  export SERVER_URL
}

function print_process() {
  echo $(<"$PIDFile")
}

function is_server_up() {
  log "Polling for server at $SERVER_URL..."
  curl --output /dev/null --silent --head --fail --insecure $SERVER_URL
}

function run() {
  CMD="$1"; shift
  case "$CMD" in
    start)
        java -jar $JARFile --spring.profiles.active=e2e 2>&1 > $LOGFile &
        ;;
    wait_until_started)
        retryable_condition 'is_server_up' 120 || fail "Failed to start application"
        ;;
    test)
        $NODE_MODULES/protractor/bin/webdriver-manager update
        $NODE_MODULES/protractor/bin/protractor $SCRIPT_DIR/protractor.conf.js "$@" || fail "There are test failures"
        ;;
    debug)
        $NODE_MODULES/protractor/bin/webdriver-manager update
        $NODE_MODULES/protractor/bin/protractor debug $SCRIPT_DIR/protractor.conf.js "$@" || fail "There are test failures"
        ;;
    stop)
       kill -TERM $(print_process)
       ;;
    verify)
       grep --quiet '<failure' $SCRIPT_DIR/protractor-results.xml && fail "There are test failures"
  esac
}

source $(dirname $0)/../scripts/main.inc