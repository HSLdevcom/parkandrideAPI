create table hub (
  id bigint not null,
  name_fi varchar(255) not null,
  name_sv varchar(255) not null,
  name_en varchar(255) not null,
  location geometry not null,

  street_address_fi varchar(255),
  street_address_sv varchar(255),
  street_address_en varchar(255),

  postal_code varchar(5),

  city_fi varchar(255),
  city_sv varchar(255),
  city_en varchar(255),

  primary key (id)
);

create sequence hub_id_seq increment by 1 start with 1;

create table hub_facility (
  hub_id bigint not null,
  facility_id bigint not null,

  primary key (hub_id, facility_id),

  constraint hub_facility_hub_id_fk foreign key (hub_id)
    references hub (id)
);
