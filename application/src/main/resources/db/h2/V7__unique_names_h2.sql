alter table operator alter column name_fi VARCHAR_IGNORECASE(255);
alter table operator alter column name_sv VARCHAR_IGNORECASE(255);
alter table operator alter column name_en VARCHAR_IGNORECASE(255);

alter table contact alter column name_fi VARCHAR_IGNORECASE(255);
alter table contact alter column name_sv VARCHAR_IGNORECASE(255);
alter table contact alter column name_en VARCHAR_IGNORECASE(255);

alter table facility alter column name_fi VARCHAR_IGNORECASE(255);
alter table facility alter column name_sv VARCHAR_IGNORECASE(255);
alter table facility alter column name_en VARCHAR_IGNORECASE(255);

alter table hub alter column name_fi VARCHAR_IGNORECASE(255);
alter table hub alter column name_sv VARCHAR_IGNORECASE(255);
alter table hub alter column name_en VARCHAR_IGNORECASE(255);


create unique index operator_name_fi_u on operator (name_fi);
create unique index operator_name_sv_u on operator (name_sv);
create unique index operator_name_en_u on operator (name_en);

create unique index contact_name_fi_u on contact (name_fi);
create unique index contact_name_sv_u on contact (name_sv);
create unique index contact_name_en_u on contact (name_en);


create unique index facility_name_fi_u on facility (name_fi);
create unique index facility_name_sv_u on facility (name_sv);
create unique index facility_name_en_u on facility (name_en);

create unique index hub_name_fi_u on hub (name_fi);
create unique index hub_name_sv_u on hub (name_sv);
create unique index hub_name_en_u on hub (name_en);
