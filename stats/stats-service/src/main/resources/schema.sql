DROP TABLE IF EXISTS stat CASCADE;

CREATE TABLE stat(
                     id INTEGER NOT NULL  GENERATED ALWAYS AS IDENTITY  PRIMARY KEY,
                     app VARCHAR(50) NOT NULL,
                     uri VARCHAR(150) NOT NULL,
                     ip VARCHAR(15) NOT NULL,
                     timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
