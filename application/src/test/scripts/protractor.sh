#!/bin/bash

JARFile="../../../target/parkandride-application-0.0.1-SNAPSHOT.jar"
PIDFile="application.pid"
LOGFile="application.log"
SERVER_URL=localhost:8080

function print_process() {
  echo $(<"$PIDFile")
}

function retryable_condition() {
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

function is_server_up() {
  curl --output /dev/null --silent --head --fail $SERVER_URL
}

case "$1" in
  start)
      java -jar $JARFile 2>&1 > $LOGFile &
      echo "Process started"
      ;;
  wait_until_started)
      retryable_condition 'is_server_up' 60
      ;;
  stop)
     kill -TERM $(print_process)
esac

exit 0