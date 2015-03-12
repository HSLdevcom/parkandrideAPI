#!/bin/bash
set -eu
: ${ENV:?}
: ${VAULT_PASSWORD:?}
set -x

VERSION=`cat version`
BINARY=`readlink -f staging/fi/hsl/parkandride/parkandride-application/$VERSION/parkandride-application-$VERSION.jar`

cd deploy
echo '#!/bin/bash
set -eu
echo "$VAULT_PASSWORD"
' > vault-password
chmod a+x vault-password
./ansible-playbook site.yml -l "$ENV" -e "app_binary=$BINARY"
