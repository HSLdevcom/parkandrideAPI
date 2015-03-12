#!/bin/bash
set -eu
: ${ENV:?}
: ${VAULT_PASS:?}
set -x

VERSION=`cat version`
BINARY=`readlink -f staging/fi/hsl/parkandride/parkandride-application/$VERSION/parkandride-application-$VERSION.jar`

cd deploy
echo '#!/bin/bash
set -eu
echo "$VAULT_PASS"
' > vault-pass
chmod a+x vault-pass
./ansible-playbook site.yml -l "$ENV" -e "app_binary=$BINARY"
