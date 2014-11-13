#!/bin/bash

set -x

VERSION=`cat version`

PROTRACTOR_DIR="etc/protractor"
cd $PROTRACTOR_DIR
APP_JAR="../../staging/fi/hsl/parkandride/parkandride-application/$VERSION/parkandride-application-$VERSION.jar"
export APP_JAR

[ -n "$WITH_XVFB" ] && /etc/init.d/xvfb start
bash protractor.sh start
bash protractor.sh wait_until_started
bash protractor.sh test
bash protractor.sh stop
[ -n "$WITH_XVFB" ] && /etc/init.d/xvfb stop
bash protractor.sh verify



