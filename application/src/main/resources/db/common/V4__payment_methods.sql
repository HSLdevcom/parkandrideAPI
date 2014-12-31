create table payment_method (
  id bigint not null,

  name_fi varchar(255) not null,
  name_sv varchar(255) not null,
  name_en varchar(255) not null,

  primary key (id)
);

insert into payment_method (id, name_fi, name_sv, name_en) values (1, 'Kolikko', 'Coins', 'Coins');
insert into payment_method (id, name_fi, name_sv, name_en) values (2, 'Seteli', 'Notes', 'Notes');
insert into payment_method (id, name_fi, name_sv, name_en) values (3, 'Visa Debit', 'Visa Debit', 'Visa Debit');
insert into payment_method (id, name_fi, name_sv, name_en) values (4, 'Visa Electron', 'Visa Electron', 'Visa Electron');
insert into payment_method (id, name_fi, name_sv, name_en) values (5, 'American Express', 'American Express', 'American Express');
insert into payment_method (id, name_fi, name_sv, name_en) values (6, 'MasterCard', 'MasterCard', 'MasterCard');
insert into payment_method (id, name_fi, name_sv, name_en) values (7, 'DinersClub', 'DinersClub', 'DinersClub');
insert into payment_method (id, name_fi, name_sv, name_en) values (8, 'HSL matkakortti', 'HSL travel card', 'HSL travel card');
insert into payment_method (id, name_fi, name_sv, name_en) values (9, 'VR matkakortti', 'VR card', 'VR card');
insert into payment_method (id, name_fi, name_sv, name_en) values (10, 'HSL kertalippu', 'HSL single ticket', 'HSL single ticket');
insert into payment_method (id, name_fi, name_sv, name_en) values (11, 'VR kertalippu', 'VR single ticket', 'VR single ticket');
insert into payment_method (id, name_fi, name_sv, name_en) values (12, 'Muu', 'Other', 'Other');
