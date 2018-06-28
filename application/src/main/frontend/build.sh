#!/usr/bin/env bash
set -eu

readonly IMAGE="liipi-web"
readonly BUILDER_IMAGE="${IMAGE}-builder"

# do the build
echo "### Build builder image ###"
docker build --tag "${BUILDER_IMAGE}" --target builder .
echo
echo "### Build production image ###"
docker build --tag "${IMAGE}" .

# update local Yarn offline mirror
temp_container=copy-yarn-offline-mirror
docker run --name "${temp_container}" "${BUILDER_IMAGE}" /bin/true
docker cp "${temp_container}:/opt/app/yarn-offline-mirror" .
docker rm "${temp_container}"
