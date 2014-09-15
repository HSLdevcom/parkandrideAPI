create table facility (
  id bigint not null,
  name varchar(255) not null,
  border polygon not null,

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
