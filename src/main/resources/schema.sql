DROP TABLE IF EXISTS product;
CREATE TABLE if not exists product(
   id serial PRIMARY KEY,
   description VARCHAR (500),
   price numeric (10,2) NOT NULL
);