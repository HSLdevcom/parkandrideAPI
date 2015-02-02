create table facility_status (
  name varchar(64) not null,
  primary key (name)
);

insert into facility_status values ('IN_OPERATION');
insert into facility_status values ('INACTIVE');
insert into facility_status values ('TEMPORARILY_CLOSED');
insert into facility_status values ('EXCEPTIONAL_SITUATION');


create table capacity_type (
  name varchar(64) not null,
  primary key (name)
);

insert into capacity_type values ('CAR');
insert into capacity_type values ('BICYCLE');
insert into capacity_type values ('BICYCLE_LOCKUP');
insert into capacity_type values ('DISABLED');
insert into capacity_type values ('MOTORCYCLE');
insert into capacity_type values ('ELECTRIC_CAR');


create table day_type (
  name varchar(64) not null,
  primary key (name)
);

insert into day_type values ('BUSINESS_DAY');
insert into day_type values ('SATURDAY');
insert into day_type values ('SUNDAY');
insert into day_type values ('HOLIDAY');
insert into day_type values ('EVE');


create table usage (
  name varchar(64) not null,
  primary key (name)
);
insert into usage values ('PARK_AND_RIDE');
insert into usage values ('COMMERCIAL');


create table utilization_status (
  name varchar(64) not null,

  primary key (name)
);

insert into utilization_status values ('FULL');
insert into utilization_status values ('SPACES_AVAILABLE');


create table service (
  name varchar(64) not null,
  primary key (name)
);

insert into service values ('ELEVATOR');
insert into service values ('TOILETS');
insert into service values ('ACCESSIBLE_TOILETS');
insert into service values ('LIGHTING');
insert into service values ('COVERED');
insert into service values ('SURVEILLANCE_CAMERAS');
insert into service values ('VENDING_MACHINE');
insert into service values ('INFORMATION_POINT');
insert into service values ('PAY_DESK');
insert into service values ('CAR_WASH');
insert into service values ('REPAIR_SHOP');
insert into service values ('SHOE_SHINE');
insert into service values ('PAYMENT_AT_GATE');
insert into service values ('UMBRELLA_RENTAL');
insert into service values ('PARKING_SPACE_RESERVATION');
insert into service values ('ENGINE_IGNITION_AID');
insert into service values ('FIRST_AID');
insert into service values ('STROLLER_RENTAL');
insert into service values ('INFO_SCREENS');


create table payment_method (
  name varchar(64) not null,
  primary key (name)
);

insert into payment_method values ('COINS');
insert into payment_method values ('NOTES');
insert into payment_method values ('VISA_DEBIT');
insert into payment_method values ('VISA_ELECTRON');
insert into payment_method values ('AMERICAN_EXPRESS');
insert into payment_method values ('MASTERCARD');
insert into payment_method values ('DINERS_CLUB');
insert into payment_method values ('HSL_TRAVEL_CARD');
insert into payment_method values ('HSL_SINGLE_TICKET');
insert into payment_method values ('VR_SINGLE_TICKET');
insert into payment_method values ('OTHER');
