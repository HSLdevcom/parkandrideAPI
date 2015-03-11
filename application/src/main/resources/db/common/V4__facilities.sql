create table facility (
  id bigint not null,
  name_fi varchar(255) not null,
  name_sv varchar(255) not null,
  name_en varchar(255) not null,
-- location -> V4_1__facilities.sql
  operator_id bigint not null,
  status varchar(64) not null,
  pricing_method varchar(64) not null,

  status_description_fi varchar(255),
  status_description_sv varchar(255),
  status_description_en varchar(255),

  emergency_contact_id bigint not null,
  operator_contact_id bigint not null,
  service_contact_id bigint,

  payment_info_detail_fi varchar(255),
  payment_info_detail_sv varchar(255),
  payment_info_detail_en varchar(255),

  payment_info_url_fi varchar(255),
  payment_info_url_sv varchar(255),
  payment_info_url_en varchar(255),

  opening_hours_info_fi varchar(255),
  opening_hours_info_sv varchar(255),
  opening_hours_info_en varchar(255),

  opening_hours_url_fi varchar(255),
  opening_hours_url_sv varchar(255),
  opening_hours_url_en varchar(255),

  capacity_car int,
  capacity_disabled int,
  capacity_electric_car int,
  capacity_motorcycle int,
  capacity_bicycle int,
  capacity_bicycle_secure_space int,

  usage_park_and_ride boolean not null,
  usage_hsl boolean not null,
  usage_commercial boolean not null,

  primary key (id),

  constraint facility_operator_id_fk foreign key (operator_id)
    references operator (id),

  constraint facility_status_fk foreign key (status)
    references facility_status (name),

  constraint facility_emergency_contact_id_fk foreign key (emergency_contact_id)
    references contact (id),

  constraint facility_operator_contact_id_fk foreign key (operator_contact_id)
    references contact (id),

  constraint facility_service_contact_id_fk foreign key (service_contact_id)
    references contact (id),

  constraint facility_pricing_method_fk foreign key (pricing_method)
    references pricing_method (name)
);

create sequence facility_id_seq increment by 1 start with 1;

create index facility_operator_id_idx on facility (operator_id);

create index facility_status_idx on facility (status);


create table facility_alias (
  facility_id bigint not null,
  alias varchar(255) not null,

  primary key (facility_id, alias),

  constraint facility_alias_fk foreign key (facility_id)
    references facility (id)
);

create index facility_alias_facility_id_idx on facility_alias (facility_id);


create table port (
  facility_id bigint not null,
  port_index int not null,
  entry boolean not null,
  exit boolean not null,
  pedestrian boolean not null,
  bicycle boolean not null,
-- location -> V4_1__facilities.sql

  street_address_fi varchar(255),
  street_address_sv varchar(255),
  street_address_en varchar(255),

  postal_code varchar(5),

  city_fi varchar(255),
  city_sv varchar(255),
  city_en varchar(255),

  info_fi varchar(255),
  info_sv varchar(255),
  info_en varchar(255),

  primary key (facility_id, port_index),

  constraint port_facility_id_fk foreign key (facility_id)
    references facility (id)
);

create index port_facility_id_idx on port (facility_id);


create table facility_service (
  facility_id bigint not null,
  service varchar(64) not null,

  primary key (facility_id, service),

  constraint facility_service_facility_id_fk foreign key (facility_id)
    references facility (id),

  constraint facility_service_service_fk foreign key (service)
    references service (name)
);

create index facility_service_facility_id_idx on facility_service (facility_id);


create table facility_utilization (
  facility_id bigint not null,
  capacity_type varchar(64) not null,
  usage varchar(64) not null,
  ts timestamp not null,
  spaces_available int not null,

  constraint facility_utilization_facility_id_fk foreign key (facility_id)
    references facility (id),

  constraint facility_utilization_capacity_type_fk foreign key (capacity_type)
    references capacity_type (name),

  constraint facility_utilization_usage_fk foreign key (usage)
  references usage (name)
);
-- create index on facility_utilization -> V4_1__facilities.sql


create table facility_payment_method (
  facility_id bigint not null,
  payment_method varchar(64) not null,

  primary key (facility_id, payment_method),

  constraint facility_payment_method_facility_id_fk foreign key (facility_id)
    references facility (id),

  constraint facility_payment_method_payment_method_fk foreign key (payment_method)
    references payment_method (name)
);

create index facility_payment_method_facility_id_idx on facility_payment_method (facility_id);


create table pricing (
  facility_id bigint not null,

  capacity_type varchar(64) not null,
  usage varchar(64) not null,
  max_capacity int not null,

  day_type varchar(64) not null,
  from_time smallint not null,
  until_time smallint not null,

  price_fi varchar(255),
  price_sv varchar(255),
  price_en varchar(255),

  primary key (facility_id, capacity_type, usage, day_type, from_time),

  constraint pricing_facility_id_fk foreign key (facility_id)
  references facility (id),

  constraint pricing_capacity_type_fk foreign key (capacity_type)
  references capacity_type (name),

  constraint pricing_usage_fk foreign key (usage)
  references usage (name),

  constraint pricing_day_type_fk foreign key (day_type)
  references day_type (name)
);

create sequence pricing_id_seq increment by 1 start with 1;

create index pricing_facility_id_idx on pricing (facility_id);


create table unavailable_capacity (
  facility_id bigint not null,
  capacity_type varchar(64) not null,
  usage varchar(64) not null,
  capacity int not null,

  primary key (facility_id, capacity_type, usage),

  constraint unavailable_capacity_facility_id_fk foreign key (facility_id)
  references facility (id),

  constraint unavailable_capacity_usage_fk foreign key (usage)
  references usage (name),

  constraint unavailable_capacity_capacity_type_fk foreign key (capacity_type)
  references capacity_type (name)
);

create index unavailable_capacity_facility_id_idx on unavailable_capacity (facility_id);
