#!/bin/bash

set -eu
: ${TARGET_SERVER_URL:?}
set -x

export SERVER_URL=$TARGET_SERVER_URL

PROTRACTOR_DIR="protractor"
cd $PROTRACTOR_DIR

/etc/init.d/xvfb start
bash protractor.sh wait_until_started
bash protractor.sh test
etc/init.d/xvfb stop
bash protractor.sh verify
