create table facility (
  id bigint not null,
  name_fi varchar(255) not null,
  name_sv varchar(255) not null,
  name_en varchar(255) not null,
  location geometry not null,

  primary key (id)
);

create sequence facility_id_seq increment by 1 start with 1;


create table facility_alias (
  facility_id bigint not null,
  alias varchar(255) not null,

  primary key (facility_id, alias),

  constraint facility_alias_fk foreign key (facility_id)
    references facility (id)
);


create table capacity_type (
  name varchar(64) not null,

  primary key (name)
);

insert into capacity_type values ('CAR');
insert into capacity_type values ('BICYCLE');
insert into capacity_type values ('PARK_AND_RIDE');
insert into capacity_type values ('DISABLED');
insert into capacity_type values ('MOTORCYCLE');
insert into capacity_type values ('ELECTRIC_CAR');


create table capacity (
  facility_id bigint not null,
  capacity_type varchar(64) not null,
  built int not null,
  unavailable int not null,

  primary key (facility_id, capacity_type),

  constraint capacity_facility_id_fk foreign key (facility_id)
    references facility (id),

  constraint capacity_capacity_type_fk foreign key (capacity_type)
    references capacity_type (name)
);

create table port (
  facility_id bigint not null,
  port_index int not null,
  entry boolean not null,
  exit boolean not null,
  pedestrian boolean not null,
  bicycle boolean not null,
  location geometry not null,

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

create table facility_service (
  facility_id bigint not null,
  service_id bigint not null,

  primary key (facility_id, service_id),

  constraint facility_service_facility_id_fk foreign key (facility_id)
    references facility (id),

  constraint facility_service_service_id_fk foreign key (service_id)
    references service (id)
);


create table contact_type (
  name varchar(64) not null,

  primary key (name)
);

insert into contact_type values ('EMERGENCY');
insert into contact_type values ('CUSTOMER_SERVICE');

create table facility_contact (
  facility_id bigint not null,
  contact_type varchar(64) not null,
  contact_id bigint not null,

  primary key (facility_id, contact_type, contact_id),

  constraint facility_contact_facility_id_fk foreign key (facility_id)
    references facility (id),

  constraint facility_contact_type_fk foreign key (contact_type)
    references contact_type (name),

  constraint facility_contact_contact_id_fk foreign key (contact_id)
    references contact (id)
);
