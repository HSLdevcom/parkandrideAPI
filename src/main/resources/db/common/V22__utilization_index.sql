DROP INDEX facility_utilization_idx;
CREATE INDEX facility_utilization_idx
  ON facility_utilization (facility_id, capacity_type, usage, ts);
