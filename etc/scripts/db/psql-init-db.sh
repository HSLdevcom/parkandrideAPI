#!/bin/bash

function init() {
  export PGCLIENTENCODING="UTF8"
  : ${LIIPI_DB:="liipidb"}
  : ${LIIPI_SCHEMA:="liipi"}
  : ${LIIPI_USER:="liipi"}
  : ${LIIPI_PASS:="liipipw"}

  PSQL_OPTS=("-v" "ON_ERROR_STOP=1")
  for arg do
    case "$arg" in
      --schema-only) SCHEMA_ONLY=1 ;;
      *) PSQL_OPTS+=("$arg") ;;
    esac
  done
}

function create_db() {
  psql "${PSQL_OPTS[@]}" <<EOF
DROP DATABASE IF EXISTS $LIIPI_DB;
CREATE DATABASE $LIIPI_DB ENCODING 'UTF8' LC_COLLATE 'fi_FI.UTF-8' LC_CTYPE 'fi_FI.UTF-8' TEMPLATE template0;
\connect $LIIPI_DB
CREATE EXTENSION postgis;
CREATE EXTENSION postgis_topology;
CREATE EXTENSION fuzzystrmatch;
CREATE EXTENSION postgis_tiger_geocoder;
\q
EOF
}

function create_user() {
  psql "${PSQL_OPTS[@]}" <<EOF
DROP USER IF EXISTS $LIIPI_USER;
CREATE USER $LIIPI_USER WITH PASSWORD '$PASS';
GRANT CONNECT, TEMP ON DATABASE $LIIPI_DB TO $LIIPI_USER;
EOF
}

function create_schema() {
  psql "${PSQL_OPTS[@]}" <<EOF
\connect $LIIPI_DB
DROP SCHEMA IF EXISTS $LIIPI_SCHEMA;
CREATE SCHEMA $LIIPI_SCHEMA;
GRANT ALL PRIVILEGES ON SCHEMA $LIIPI_SCHEMA TO $LIIPI_USER;
\q
EOF
}

function run() {
  if [ -z "$SCHEMA_ONLY" ] ; then
    create_db
    create_user
  fi
  create_schema
}

VERBOSE="true"
source $(dirname $0)/../main.inc

