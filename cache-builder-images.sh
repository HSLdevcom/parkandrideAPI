#!/usr/bin/env bash
set -eu

for name in "liipi-api-builder-cache" "liipi-web-builder-cache" "liipi-web-nginx-builder-cache"; do
    latest_id=$(docker images --filter "label=cache-this-layer=${name}" --format "{{.ID}}" | head -n 1)
    docker tag "${latest_id}" "${name}"
    echo "Tagged ${latest_id} as ${name}"
done
