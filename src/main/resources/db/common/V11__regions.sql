create table region (
  id bigint not null,
  name_fi varchar(255) not null,
  name_sv varchar(255) not null,
  name_en varchar(255) not null,
-- area -> V11_1__regions.sql

  primary key (id)
);