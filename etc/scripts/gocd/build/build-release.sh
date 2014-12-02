#!/bin/bash

function init_db {
  bash $ROOT_DIR/etc/scripts/db/psql-init-db.sh -h dev.cvokarbgtqbl.eu-west-1.rds.amazonaws.com -U devmaster postgres
}

function init() {
  set -eu
  : ${GO_PIPELINE_COUNTER:?}
  : ${PWD:?}
  : ${LIIPI_DB:="liipici"}

  ROOT_DIR="$SCRIPT_DIR/../../../.."

  cd "$ROOT_DIR/etc/protractor" && npm install
  [ "$INIT_DB" = "yes" ] && init_db
  cd $ROOT_DIR

  export PSQL_USERNAME=$LIIPI_DB SPRING_PROFILES_ACTIVE=env_gocd
}

function version() {
  local byxpath=".//{http://maven.apache.org/POM/4.0.0}parent/{http://maven.apache.org/POM/4.0.0}version"
  local version=$(python -c "import xml.etree.ElementTree as ET; print ET.parse('pom.xml').find(\"$byxpath\").text")
  echo $version > version
  sed -i -e "s/SNAPSHOT$/$GO_PIPELINE_COUNTER/" version
  cat version
}

function run() {
  local v=`version`

  mvn versions:set \
      --update-snapshots \
      --batch-mode \
      --errors \
      -DgenerateBackupPoms=false \
      -DnewVersion="$v" \
      --file parent/pom.xml

  mvn clean deploy \
      --update-snapshots \
      --batch-mode \
      --errors \
      -DaltDeploymentRepository="staging::default::file:staging"
}

VERBOSE="true"
source $(dirname $0)/../../main.inc
