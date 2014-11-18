#!/bin/bash

function init() {
  export PGCLIENTENCODING="UTF8"
  DB="liipidb"
  SCHEMA="liipi"
  USER="liipi"
  PASS="liipipw"
}

function run() {
  psql -v ON_ERROR_STOP=1 "$@" <<EOF
DROP DATABASE IF EXISTS $DB;
DROP USER IF EXISTS $USER;

CREATE DATABASE $DB ENCODING 'UTF8' LC_COLLATE 'fi_FI.UTF-8' LC_CTYPE 'fi_FI.UTF-8' TEMPLATE template0;
CREATE USER $USER WITH PASSWORD '$PASS';
GRANT CONNECT, TEMP ON DATABASE $DB TO $USER;

\connect $DB

CREATE EXTENSION postgis;
CREATE EXTENSION postgis_topology;
CREATE EXTENSION fuzzystrmatch;
CREATE EXTENSION postgis_tiger_geocoder;
CREATE SCHEMA $SCHEMA;

GRANT ALL PRIVILEGES ON SCHEMA $SCHEMA TO $USER;

\q
EOF
}

VERBOSE="true"
source $(dirname $0)/../main.inc

