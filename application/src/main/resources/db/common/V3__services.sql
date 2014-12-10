create table service (
  id bigint not null,

  name_fi varchar(255) not null,
  name_sv varchar(255) not null,
  name_en varchar(255) not null,

  primary key (id)
);

insert into service (id, name_fi, name_sv, name_en) values (1, 'Hissi', 'Elevator', 'Elevator');
insert into service (id, name_fi, name_sv, name_en) values (2, 'WC', 'Toilets', 'Toilets');
insert into service (id, name_fi, name_sv, name_en) values (3, 'Esteetön WC', 'Handicapped Toilets', 'Handicapped Toilets');
insert into service (id, name_fi, name_sv, name_en) values (4, 'Valaistus', 'Lighting', 'Lighting');
insert into service (id, name_fi, name_sv, name_en) values (5, 'Katettu', 'Covered', 'Covered');
insert into service (id, name_fi, name_sv, name_en) values (6, 'Kameravalvonta', 'Surveillance cameras', 'Surveillance cameras');
insert into service (id, name_fi, name_sv, name_en) values (7, 'Lippuautomaatti', 'Vending Machine', 'Vending Machine');
insert into service (id, name_fi, name_sv, name_en) values (8, 'Infopiste', 'Information Point', 'Information Point');
insert into service (id, name_fi, name_sv, name_en) values (9, 'Maksutiski', 'Pay Desk', 'Pay Desk');
insert into service (id, name_fi, name_sv, name_en) values (10, 'Autopesu', 'Car Wash', 'Car Wash');
insert into service (id, name_fi, name_sv, name_en) values (11, 'Korjauspalvelu', 'Repair Shop', 'Repair Shop');
insert into service (id, name_fi, name_sv, name_en) values (12, 'Kengänkiillotus', 'Shoe Shine', 'Shoe Shine');
