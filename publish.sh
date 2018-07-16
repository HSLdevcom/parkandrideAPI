#!/usr/bin/env bash
set -eu
: ${1:? Usage: $0 new_tag}

readonly TAG="$1"
readonly REGISTRY="995219551342.dkr.ecr.eu-west-1.amazonaws.com"
readonly API_IMAGE="liipi-api"
readonly WEB_IMAGE="liipi-web"

#export AWS_PROFILE=liipi
eval $(aws ecr get-login --no-include-email --region eu-west-1)

set -x

docker tag "${API_IMAGE}:latest" "${REGISTRY}/${API_IMAGE}:${TAG}"
docker tag "${WEB_IMAGE}:latest" "${REGISTRY}/${WEB_IMAGE}:${TAG}"

docker push "${REGISTRY}/${API_IMAGE}:${TAG}"
docker push "${REGISTRY}/${WEB_IMAGE}:${TAG}"
