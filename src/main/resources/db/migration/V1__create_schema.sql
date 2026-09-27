-- Initial schema for the MyMobile app.

CREATE TABLE customer (
    cid         INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    birthday    VARCHAR(50)  NOT NULL,
    phone_lines INT          NOT NULL DEFAULT 0,
    balance     FLOAT        NOT NULL DEFAULT 0,
    planId      INT          NOT NULL DEFAULT 0
);

CREATE TABLE phones (
    pid         INT AUTO_INCREMENT PRIMARY KEY,
    `condition` VARCHAR(50)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    rating      INT          NOT NULL DEFAULT 0,
    price       FLOAT,
    color       VARCHAR(50)  NOT NULL,
    img_src     VARCHAR(255) NOT NULL COMMENT 'object key in the MinIO phones bucket',
    alt         VARCHAR(255) NOT NULL,
    quantity    INT          NOT NULL DEFAULT 0
);

CREATE TABLE phonelines (
    plid         INT AUTO_INCREMENT PRIMARY KEY,
    pid          INT NOT NULL,
    phone_number VARCHAR(20),
    cid          INT NOT NULL,
    phone_name   VARCHAR(100),
    img_src      VARCHAR(255),
    alt          VARCHAR(255),
    color        VARCHAR(50)
);

CREATE TABLE phoneplans (
    planId        INT AUTO_INCREMENT PRIMARY KEY,
    numberOfLines INT   NOT NULL,
    monthlyRate   FLOAT NOT NULL,
    description   VARCHAR(500)
);

CREATE TABLE creditcard (
    crid               INT AUTO_INCREMENT PRIMARY KEY,
    credit_card_number VARCHAR(16) NOT NULL,
    expiration_date    VARCHAR(20),
    csc                INT NOT NULL DEFAULT 0,
    cid                INT NOT NULL,
    vendor             VARCHAR(50)
);

CREATE TABLE `transaction` (
    tid    INT AUTO_INCREMENT PRIMARY KEY,
    amount FLOAT NOT NULL,
    date   VARCHAR(30),
    crid   INT NOT NULL,
    cid    INT NOT NULL
);
