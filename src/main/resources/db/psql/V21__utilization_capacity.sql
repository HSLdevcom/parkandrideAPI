ALTER TABLE facility_utilization
  ADD COLUMN capacity INT;

-- initialize capacity based on facility_capacity_history (only for cars; the others don't have production data)
UPDATE facility_utilization u
SET capacity = h.capacity_car
FROM facility_capacity_history h
WHERE u.facility_id = h.facility_id
      AND u.ts >= h.start_ts
      AND (u.ts < h.end_ts OR h.end_ts IS NULL)
      AND u.capacity_type = 'CAR';

-- initialize NULLs and fix too small capacities
UPDATE facility_utilization
SET capacity = greatest(capacity, spaces_available);

ALTER TABLE facility_utilization
  ALTER COLUMN capacity SET NOT NULL;
