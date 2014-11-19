create table service (
  id bigint not null,

  name_fi varchar(255) not null,
  name_sv varchar(255) not null,
  name_en varchar(255) not null,

  primary key (id)
);

insert into service (id, name_fi, name_sv, name_en) values (1, 'Elevator', 'Elevator', 'Elevator');
insert into service (id, name_fi, name_sv, name_en) values (2, 'Toilets', 'Toilets', 'Toilets');
insert into service (id, name_fi, name_sv, name_en) values (3, 'Handicapped Toilets', 'Handicapped Toilets', 'Handicapped Toilets');
insert into service (id, name_fi, name_sv, name_en) values (4, 'Lighting', 'Lighting', 'Lighting');
insert into service (id, name_fi, name_sv, name_en) values (5, 'Covered', 'Covered', 'Covered');
insert into service (id, name_fi, name_sv, name_en) values (6, 'Surveillance cameras', 'Surveillance cameras', 'Surveillance cameras');
insert into service (id, name_fi, name_sv, name_en) values (7, 'Vending Machine', 'Vending Machine', 'Vending Machine');
insert into service (id, name_fi, name_sv, name_en) values (8, 'Information Point', 'Information Point', 'Information Point');
insert into service (id, name_fi, name_sv, name_en) values (9, 'Pay Desk', 'Pay Desk', 'Pay Desk');
insert into service (id, name_fi, name_sv, name_en) values (10, 'Car Wash', 'Car Wash', 'Car Wash');
insert into service (id, name_fi, name_sv, name_en) values (11, 'Repair Shop', 'Repair Shop', 'Repair Shop');
insert into service (id, name_fi, name_sv, name_en) values (12, 'Shoe Shine', 'Shoe Shine', 'Shoe Shine');
