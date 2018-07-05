
insert into app_user (id, username, role, password)
  values (nextval('user_id_seq'), 'admin', 'ADMIN', '62R8AvTSPXWn2U0x8cnUhB/DKKVYPjFnQlFk8yVJhPl/fcB1GI1JINEewCNPfS9J');

insert into operator (id, name_fi, name_sv, name_en)
  values (nextval('operator_id_seq'), 'X-Park', 'X-Park', 'X-Park');

insert into app_user (id, username, role, operator_id, password)
  values (nextval('user_id_seq'), 'operator', 'OPERATOR', 1, 'rxHAdPM+iHVDY4OUfyVYPik9/z7Y7zoR4Q+Co/qHw+rGKyJPxcYSaATgoNJ5KRHW');

insert into contact (id, name_fi, name_sv, name_en, phone)
  values (nextval('contact_id_seq'), 'test', 'test', 'test', '09123456');
