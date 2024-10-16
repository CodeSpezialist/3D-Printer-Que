CREATE TABLE IF NOT EXISTS access_tokens
(
    id              INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
    access_token    VARCHAR(200)           NOT NULL,
    email           VARCHAR(255)           NOT NULL,
    expiration_date DATETIME               NOT NULL
);