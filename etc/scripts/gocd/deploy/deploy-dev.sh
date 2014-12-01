#!/bin/bash

# NOTE: this script is expectd to be run from project checkout dir

function init() {
  set -eu
  : ${DST_ENV:?}
  : ${PORT:?}

  ROOT_DIR="$SCRIPT_DIR/../../../.."

  VERSION=`cat $ROOT_DIR/version`
  AWS_TEST=ubuntu@54.77.55.69
  SSH_OPTS="-i /var/go/hsl-liipi.pem -oStrictHostKeyChecking=no"

  SRC_JAR="$ROOT_DIR/staging/fi/hsl/parkandride/parkandride-application/$VERSION/parkandride-application-$VERSION.jar"
  DST_JAR="parkandride-application-$DST_ENV-$VERSION.jar"
}

function stop_and_delete() {
  ssh $SSH_OPTS $AWS_TEST "pkill -F $DST_ENV/application.pid; sleep 1; rm -rfv $DST_ENV; mkdir $DST_ENV"
}

function transfer() {
  scp $SSH_OPTS $SRC_JAR $AWS_TEST:$DST_ENV/$DST_JAR
}

function start() {
  ssh $SSH_OPTS $AWS_TEST "cd $DST_ENV; nohup java -jar $DST_JAR --spring.profiles.active=env_$DST_ENV --server.port=$PORT > app.out &"
}

function init_db {
  LIIPI_DB=liipi${DST_ENV} bash $ROOT_DIR/etc/scripts/db/psql-init-db.sh -h dev.cvokarbgtqbl.eu-west-1.rds.amazonaws.com -U devmaster postgres
}

function run() {
  stop_and_delete
  [ "$INIT_DB" = "yes" ] && init_db
  transfer
  start
}

VERBOSE="true"
source $(dirname $0)/../../main.inc
