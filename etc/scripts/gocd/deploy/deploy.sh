#!/bin/bash
set -eu
: ${ENV:?}
set -x

VERSION=`cat version`
BINARY=`readlink -f staging/fi/hsl/parkandride/parkandride-application/$VERSION/parkandride-application-$VERSION.jar`

cd deploy
./ansible-playbook site.yml -l "$ENV" -e "app_binary=$BINARY"
