CREATE TABLE request_log (
  source VARCHAR(128) NOT NULL,
  url    VARCHAR(128) NOT NULL,
  ts     TIMESTAMP    NOT NULL,
  count  BIGINT       NOT NULL,

  PRIMARY KEY (source, url, ts)
);
