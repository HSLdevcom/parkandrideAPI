alter table facility add column location geometry not null;

alter table port add column location geometry not null;

create index facility_utilization_idx on facility_utilization (ts, facility_id, capacity_type, usage, spaces_available);
