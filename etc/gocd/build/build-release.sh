#!/bin/bash

set -eu
: ${GO_PIPELINE_COUNTER:?}
: ${PWD:?}
set -x



function version() {
  local version=`python -c 'import xml.etree.ElementTree as ET; print ET.parse("pom.xml").find("//{http://maven.apache.org/POM/4.0.0}parent/{http://maven.apache.org/POM/4.0.0}version").text'`
  echo $version > version
  sed -i -e "s/SNAPSHOT$/$GO_PIPELINE_COUNTER/" version
  cat version
}

function revision() {
  echo `git rev-parse HEAD` > revision
  cat revision
}

VERSION=`version`
REVISION=`revision`

exit 0

mvn versions:set \
    --update-snapshots \
    --batch-mode \
    --errors \
    -DgenerateBackupPoms=false \
    -DnewVersion="$VERSION" \
    --file parent/pom.xml

mvn clean deploy \
    --update-snapshots \
    --batch-mode \
    --errors \
    -DaltDeploymentRepository="staging::default::file:staging"