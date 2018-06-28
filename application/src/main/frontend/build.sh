#!/usr/bin/env bash
set -eu

readonly CACHE_FILE=".yarn-cache.tgz"
readonly CACHED_LOCK_FILE=".yarn-cache.lock"
readonly IMAGE="liipi-web"
readonly BUILDER_IMAGE="${IMAGE}-builder"

# initialize an empty cache file
if [[ ! -f "${CACHE_FILE}" ]]; then
    echo "Init empty ${CACHE_FILE}"
    tar -cvzf "${CACHE_FILE}" --files-from /dev/null
fi

# do the build
echo "### Build builder image ###"
docker build --tag "${BUILDER_IMAGE}" --target builder .
echo
echo "### Build production image ###"
docker build --tag "${IMAGE}" .

# update cache if dependencies have changed
touch -a "${CACHED_LOCK_FILE}"
cached_lock=$(cat "${CACHED_LOCK_FILE}")
latest_lock=$(docker run --rm "${BUILDER_IMAGE}" cat /tmp/yarn.lock)
if [[ "${cached_lock}" != "${latest_lock}" ]]; then
    echo "Update ${CACHE_FILE} from current build"
    docker run --rm "${BUILDER_IMAGE}" tar -czf - /home/node/.cache > "${CACHE_FILE}"
    echo "${latest_lock}" > "${CACHED_LOCK_FILE}"
fi
