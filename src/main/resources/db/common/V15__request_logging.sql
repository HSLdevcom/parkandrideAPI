CREATE TABLE request_log_source (
  id     BIGINT NOT NULL,
  source VARCHAR(128) UNIQUE,
  PRIMARY KEY (id)
);

CREATE TABLE request_log_url (
  id  BIGINT NOT NULL,
  url VARCHAR(128) UNIQUE,
  PRIMARY KEY (id)
);

CREATE TABLE request_log (
  source_id BIGINT    NOT NULL,
  url_id    BIGINT    NOT NULL,
  ts        TIMESTAMP NOT NULL,
  count     BIGINT    NOT NULL,

  PRIMARY KEY (source_id, url_id, ts),

  CONSTRAINT request_log_source_id FOREIGN KEY (source_id)
  REFERENCES request_log_source (id),

  CONSTRAINT request_log_url_id FOREIGN KEY (url_id)
  REFERENCES request_log_url (id)
);

CREATE SEQUENCE request_log_source_id_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE request_log_url_id_seq INCREMENT BY 1 START WITH 1;

