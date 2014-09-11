create table facility (
  id bigint not null,
  name varchar(255) not null,
  border polygon not null,

  primary key (id),
  unique (name)
);

create sequence facility_id_seq increment by 1 start with 1;
