#!/bin/bash

set -x

VERSION=`cat version`

PROTRACTOR_DIR="application/src/main/frontend/protractor"
cd $PROTRACTOR_DIR
APP_JAR="../../../../../staging/fi/hsl/parkandride/parkandride-application/$VERSION/parkandride-application-$VERSION.jar"
export APP_JAR

bash protractor.sh start
bash protractor.sh wait_until_started
bash protractor.sh test
bash protractor.sh stop
bash protractor.sh verify


