create table hub (
  id bigint not null,
  name varchar(255) not null,
  location geometry not null,

  primary key (id)
);

create sequence hub_id_seq increment by 1 start with 100000;

create table hub_facility (
  hub_id bigint not null,
  facility_id bigint not null,

  primary key (hub_id, facility_id),

  constraint hub_facility_hub_id_fk foreign key (hub_id)
    references hub (id)
);
