#!/bin/bash

set -eu
: ${GO_PIPELINE_COUNTER:?}
: ${PWD:?}
set -x

function version() {
  mvn -Dexec.executable="echo" \
    -Dexec.args='${project.version}' \
    -Dexec.outputFile="version" \
    --non-recursive \
    --batch-mode \
    --errors \
    org.codehaus.mojo:exec-maven-plugin:1.3.1:exec ||
    { echo "Unable to determine version" 1>&2; exit 1; }

  sed -i -e "s/SNAPSHOT$/$GO_PIPELINE_COUNTER/" version
  cat version
}

function revision() {
  echo `git rev-parse HEAD` > revision
  cat revision
}

VERSION=`version`
REVISION=`revision`

mvn versions:set \
    --batch-mode \
    --errors \
    -DgenerateBackupPoms=false \
    -DnewVersion="$VERSION" \
    --file parent/pom.xml

mvn clean deploy \
    --batch-mode \
    --errors \
    -DaltDeploymentRepository="staging::default::file:staging"