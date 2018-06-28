#!/usr/bin/env bash
set -eu

readonly CACHE_FILE=".yarn-cache.tgz"
readonly CACHED_LOCK_FILE=".yarn-cache.lock"
readonly IMAGE="liipi-web"

# initialize an empty cache file
if [[ ! -f "${CACHE_FILE}" ]]; then
    echo "Init empty ${CACHE_FILE}"
    tar -cvzf "${CACHE_FILE}" --files-from /dev/null
fi

# do the build
docker build --tag "${IMAGE}:latest" .

# update cache if dependencies have changed
cached_lock=$(cat "${CACHED_LOCK_FILE}")
latest_lock=$(docker run --rm "${IMAGE}" cat /tmp/yarn.lock)
if [[ "${cached_lock}" != "${latest_lock}" ]]; then
    echo "Update ${CACHE_FILE} from current build"
    docker run --rm "${IMAGE}" tar -czf - /home/node/.cache > "${CACHE_FILE}"
    echo "${latest_lock}" > "${CACHED_LOCK_FILE}"
fi
