#!/bin/bash

fullpath() {
  pth=$1
  case $pth in
    /*)
      ;;
    *)
      pth=`pwd`/$pth
      ;;
  esac
  echo $pth
}

SCRIPT_DIR="`fullpath \`dirname $0\``"
PATH="$SCRIPT_DIR/../node:$PATH"
JARFile=${APP_JAR:-"$SCRIPT_DIR/../../../../target/parkandride-application-0.0.1-SNAPSHOT.jar"}
PIDFile="$SCRIPT_DIR/application.pid"
LOGFile="$SCRIPT_DIR/application.log"
NODE_MODULES="$SCRIPT_DIR/../node_modules"
SERVER_URL=localhost:8080

log() {
  echo "$@"
}

fail() {
  log "ERROR" "$@"
  exit 1
}

print_process() {
  echo $(<"$PIDFile")
}

retryable_condition() {
  local condition=$1
  shift
  local max_duration=$1
  [ -z $max_duration ] && max_duration=30

  i=0
  while ! eval "$condition" ; do
    i=$(($i + 5))
    if [ $i -gt "$max_duration" ]; then
      return 1
    fi
    sleep 5
  done
  true
}

is_server_up() {
  curl --output /dev/null --silent --head --fail $SERVER_URL
}

CMD="$1"; shift
case "$CMD" in
  start)
      java -jar $JARFile 2>&1 > $LOGFile &
      ;;
  wait_until_started)
      retryable_condition 'is_server_up' 60 || fail "Failed to start application"
      ;;
  test)
      $NODE_MODULES/protractor/bin/webdriver-manager update
      $NODE_MODULES/protractor/bin/protractor $SCRIPT_DIR/protractor.conf.js "$@" || fail "There are test failures"
      ;;
  debug)
      $NODE_MODULES/protractor/bin/webdriver-manager update
      $NODE_MODULES/protractor/bin/protractor debug $SCRIPT_DIR/protractor.conf.js || fail "There are test failures"
      ;;
  stop)
     kill -TERM $(print_process)
     ;;
  verify)
     grep --quiet '<failure' $SCRIPT_DIR/protractor-results.xml && fail "There are test failures"
esac

exit 0