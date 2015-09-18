ALTER TABLE facility_prediction_history RENAME TO facility_prediction_history_old;

CREATE TABLE facility_prediction_history (
  predictor_id                 BIGINT    NOT NULL,
  ts                           TIMESTAMP NOT NULL,
  forecast_distance_in_minutes INT       NOT NULL,
  spaces_available             INT       NOT NULL,

  PRIMARY KEY (predictor_id, forecast_distance_in_minutes, ts),

  CONSTRAINT facility_prediction_history_predictor_id_fk FOREIGN KEY (predictor_id)
  REFERENCES predictor (id)
);

-- NOTE: This script relies on the fact that there was only one predictor in use
-- at the time of writing. It should not cause problems, since there are no environments
-- with multiple predictors
INSERT INTO facility_prediction_history (
  predictor_id,
  ts,
  forecast_distance_in_minutes,
  spaces_available
)
  SELECT
    p.id,
    old.ts,
    old.forecast_distance_in_minutes,
    old.spaces_available
  FROM predictor p, facility_prediction_history_old old
  WHERE p.capacity_type = old.capacity_type
        AND p.facility_id = old.facility_id
        AND p.usage = old.usage;

DROP TABLE facility_prediction_history_old;
