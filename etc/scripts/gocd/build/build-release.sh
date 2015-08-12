#!/bin/bash

function init_db {
  bash $ROOT_DIR/etc/scripts/db/psql-init-db.sh -h dev.cvokarbgtqbl.eu-west-1.rds.amazonaws.com -U devmaster postgres
}

function init() {
  set -eu
  : ${GO_PIPELINE_LABEL:?}
  : ${PWD:?}
  : ${LIIPI_DB:="liipici"}
  export LIIPI_DB PSQL_USERNAME=$LIIPI_DB SPRING_PROFILES_ACTIVE=env_gocd

  ROOT_DIR="$SCRIPT_DIR/../../../.."

  cd "$ROOT_DIR/etc/protractor" && npm install
  [ "$INIT_DB" = "yes" ] && init_db
  cd $ROOT_DIR
}

function run() {
  local version="$GO_PIPELINE_LABEL"
  echo "$version" > version

  mvn versions:set \
      --update-snapshots \
      --batch-mode \
      --errors \
      -DgenerateBackupPoms=false \
      -DnewVersion="$version" \
      --file parent/pom.xml

  mvn clean deploy \
      --update-snapshots \
      --batch-mode \
      --errors \
      -DaltDeploymentRepository="staging::default::file:staging"

  ln $(find staging -name 'parkandride-application-*.jar*') /opt/liipi-binaries/
}

VERBOSE="true"
source $(dirname $0)/../../main.inc
