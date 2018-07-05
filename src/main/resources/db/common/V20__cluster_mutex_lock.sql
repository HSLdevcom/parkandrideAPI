CREATE TABLE lock (
  name          VARCHAR(64) NOT NULL,
  owner         VARCHAR(64) NOT NULL,
  valid_until  TIMESTAMP NOT NULL,

  PRIMARY KEY (name)
);
