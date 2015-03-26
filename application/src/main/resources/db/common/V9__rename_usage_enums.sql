INSERT INTO usage VALUES ('HSL_TRAVEL_CARD');

UPDATE facility_utilization
SET usage = 'HSL_TRAVEL_CARD'
WHERE usage = 'HSL';

UPDATE pricing
SET usage = 'HSL_TRAVEL_CARD'
WHERE usage = 'HSL';

UPDATE unavailable_capacity
SET usage = 'HSL_TRAVEL_CARD'
WHERE usage = 'HSL';

UPDATE facility_prediction
SET usage = 'HSL_TRAVEL_CARD'
WHERE usage = 'HSL';

DELETE FROM usage
WHERE name = 'HSL';
