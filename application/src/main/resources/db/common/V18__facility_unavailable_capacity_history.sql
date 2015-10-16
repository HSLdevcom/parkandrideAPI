CREATE TABLE unavailable_capacity_history (
  capacity_history_id bigint not null,
  capacity_type VARCHAR(64) NOT NULL,
  usage         VARCHAR(64) NOT NULL,
  capacity      INT         NOT NULL,

  PRIMARY KEY (capacity_history_id, capacity_type, usage),

  CONSTRAINT unavailable_capacity_history_id_fk FOREIGN KEY (capacity_history_id)
  REFERENCES facility_capacity_history (id),

  CONSTRAINT unavailable_capacity_history_usage_fk FOREIGN KEY (usage)
  REFERENCES usage (name),

  CONSTRAINT unavailable_capacity_history_capacity_type_fk FOREIGN KEY (capacity_type)
  REFERENCES capacity_type (name)
);
