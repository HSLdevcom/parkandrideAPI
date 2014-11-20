# NOTE: First install Postgresql 9.3 and PostGIS 2.1 (e.g. postgresql.app)
# All parameters are passed to psql as such

export PGCLIENTENCODING="UTF8"

psql -v ON_ERROR_STOP=1 "$@" <<EOF
DROP DATABASE IF EXISTS liipidb;
DROP USER IF EXISTS liipi;

CREATE DATABASE liipidb ENCODING 'UTF8' LC_COLLATE 'fi_FI.UTF-8' LC_CTYPE 'fi_FI.UTF-8' TEMPLATE template0;
CREATE USER liipi WITH PASSWORD 'liipipwd';
GRANT CONNECT, TEMP ON DATABASE liipidb TO liipi;

\connect liipidb

CREATE EXTENSION postgis;
CREATE EXTENSION postgis_topology;
CREATE EXTENSION fuzzystrmatch;
CREATE EXTENSION postgis_tiger_geocoder;
CREATE SCHEMA liipi;

GRANT ALL PRIVILEGES ON SCHEMA liipi TO liipi;

\q
EOF
