#!/usr/bin/env bash
set -eux

docker-compose build
docker-compose up -d db
docker-compose run --rm wait-for-it db:5432 --timeout=30 || (docker-compose logs db; false)
docker-compose up -d api
docker-compose run --rm wait-for-it api:8080 --timeout=30 || (docker-compose logs api; false)
docker-compose up -d web
docker-compose run --rm wait-for-it web:80 --timeout=10 || (docker-compose logs web; false)
docker-compose run --rm test
