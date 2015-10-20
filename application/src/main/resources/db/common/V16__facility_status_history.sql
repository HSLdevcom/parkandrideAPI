CREATE TABLE facility_status_history (
  id                    BIGINT      NOT NULL,
  facility_id           BIGINT      NOT NULL,

  status                VARCHAR(64) NOT NULL,

  start_ts              TIMESTAMP   NOT NULL,
  end_ts                TIMESTAMP, -- end date may be empty when still current

  status_description_fi VARCHAR(255),
  status_description_sv VARCHAR(255),
  status_description_en VARCHAR(255),

  PRIMARY KEY (id),

  CONSTRAINT status_history_facility_id_fk FOREIGN KEY (facility_id)
  REFERENCES facility (id)
);
CREATE SEQUENCE facility_status_history_seq INCREMENT BY 1 START WITH 1;
CREATE INDEX facility_status_history_status_idx ON facility_status_history (status);
CREATE INDEX facility_status_history_start_idx ON facility_status_history (start_ts);
CREATE INDEX facility_status_history_end_idx ON facility_status_history (end_ts);
