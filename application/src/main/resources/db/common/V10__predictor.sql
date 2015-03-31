CREATE TABLE predictor (
  id                 BIGINT      NOT NULL,
  type               VARCHAR(64) NOT NULL,
  facility_id        BIGINT      NOT NULL,
  capacity_type      VARCHAR(64) NOT NULL,
  usage              VARCHAR(64) NOT NULL,
  latest_utilization TIMESTAMP   NOT NULL DEFAULT '1970-01-01 00:00:00',
  more_utilizations  BOOLEAN     NOT NULL DEFAULT TRUE,
  internal_state     TEXT        NOT NULL DEFAULT '',

  PRIMARY KEY (id),

  CONSTRAINT predictor_type_facility_capacity_usage_u UNIQUE (type, facility_id, capacity_type, usage),

  CONSTRAINT predictor_facility_id_fk FOREIGN KEY (facility_id)
  REFERENCES facility (id),

  CONSTRAINT predictor_capacity_type_fk FOREIGN KEY (capacity_type)
  REFERENCES capacity_type (name),

  CONSTRAINT predictor_usage_fk FOREIGN KEY (usage)
  REFERENCES usage (name)
);
