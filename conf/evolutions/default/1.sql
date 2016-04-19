
# Demo schema
 
# --- !Ups

CREATE SEQUENCE demo_id_seq;

CREATE TABLE demo (
    id bigint NOT NULL DEFAULT nextval('demo_id_seq'),
    img1 bytea,
    img2 bytea,
    img3 bytea,
    PRIMARY KEY (id)
);

ALTER SEQUENCE demo_id_seq OWNED BY demo.id;
 
# --- !Downs
 
DROP TABLE demo;
