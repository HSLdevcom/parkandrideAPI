alter table facility add column location geography(POLYGON, 4326) not null;

alter table port add column location geography(POINT, 4326) not null;

create index facility_location_gist ON facility USING GIST ( location );
