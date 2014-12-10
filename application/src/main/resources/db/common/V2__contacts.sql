create table contact (
  id bigint not null,

  name_fi varchar(255) not null,
  name_sv varchar(255) not null,
  name_en varchar(255) not null,

  phone varchar(255),
  email varchar(255),

  street_address_fi varchar(255),
  street_address_sv varchar(255),
  street_address_en varchar(255),

  postal_code varchar(5),

  city_fi varchar(255),
  city_sv varchar(255),
  city_en varchar(255),

  opening_hours_fi varchar(2000),
  opening_hours_sv varchar(2000),
  opening_hours_en varchar(2000),

  info_fi varchar(255),
  info_sv varchar(255),
  info_en varchar(255),

  primary key (id),

  constraint contact_email_or_phone_chk check (email <> null or phone <> null)
);

create sequence contact_id_seq increment by 1 start with 1;
