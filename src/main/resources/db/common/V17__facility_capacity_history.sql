CREATE TABLE facility_capacity_history (
  id                            BIGINT    NOT NULL,
  facility_id                   BIGINT    NOT NULL,

  start_ts                      TIMESTAMP NOT NULL,
  end_ts                        TIMESTAMP, -- end date may be empty when still current

  capacity_car                  INT,
  capacity_disabled             INT,
  capacity_electric_car         INT,
  capacity_motorcycle           INT,
  capacity_bicycle              INT,
  capacity_bicycle_secure_space INT,

  PRIMARY KEY (id),

  CONSTRAINT capacity_history_facility_id_fk FOREIGN KEY (facility_id)
  REFERENCES facility (id)
);
CREATE SEQUENCE facility_capacity_history_seq INCREMENT BY 1 START WITH 1;
CREATE INDEX facility_capacity_history_start_idx ON facility_status_history (start_ts);
CREATE INDEX facility_capacity_history_end_idx ON facility_status_history (end_ts);
