CREATE TABLE IF NOT EXISTS users
(
    email       VARCHAR(200) NOT NULL PRIMARY KEY,
    first_name  VARCHAR(60)  NOT NULL,
    last_Name   VARCHAR(60)  NOT NULL,
    is_active   BOOLEAN      NOT NULL,
    keycloak_id VARCHAR(255) NOT NULL
);