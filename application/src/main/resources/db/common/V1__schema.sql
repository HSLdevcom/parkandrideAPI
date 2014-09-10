create table facility (
  id bigint not null,
  name varchar(255) not null,
  border multipolygon not null,

  primary key (id)
);
