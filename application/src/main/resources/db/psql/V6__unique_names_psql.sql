create unique index operator_name_fi_u on operator (lower(name_fi));
create unique index operator_name_sv_u on operator (lower(name_sv));
create unique index operator_name_en_u on operator (lower(name_en));

create unique index contact_name_fi_u on contact (lower(name_fi));
create unique index contact_name_sv_u on contact (lower(name_sv));
create unique index contact_name_en_u on contact (lower(name_en));

create unique index facility_name_fi_u on facility (lower(name_fi));
create unique index facility_name_sv_u on facility (lower(name_sv));
create unique index facility_name_en_u on facility (lower(name_en));

create unique index hub_name_fi_u on hub (lower(name_fi));
create unique index hub_name_sv_u on hub (lower(name_sv));
create unique index hub_name_en_u on hub (lower(name_en));
