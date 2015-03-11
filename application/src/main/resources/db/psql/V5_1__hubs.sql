alter table hub add column location geography(POINT, 4326) not null;

create index hub_location_gist ON hub USING GIST ( location );
