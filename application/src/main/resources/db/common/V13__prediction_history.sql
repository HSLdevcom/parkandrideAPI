CREATE TABLE facility_prediction_history (
  facility_id                  BIGINT      NOT NULL,
  capacity_type                VARCHAR(64) NOT NULL,
  usage                        VARCHAR(64) NOT NULL,
  ts                           TIMESTAMP   NOT NULL,
  forecast_distance_in_minutes INT         NOT NULL,
  spaces_available             INT         NOT NULL,

  PRIMARY KEY (facility_id, capacity_type, usage, forecast_distance_in_minutes, ts),

  CONSTRAINT facility_prediction_history_facility_id_fk FOREIGN KEY (facility_id)
  REFERENCES facility (id),

  CONSTRAINT facility_prediction_history_capacity_type_fk FOREIGN KEY (capacity_type)
  REFERENCES capacity_type (name),

  CONSTRAINT facility_prediction_history_usage_fk FOREIGN KEY (usage)
  REFERENCES usage (name)
);
