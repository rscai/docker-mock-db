-- create schema
CREATE TABLESPACE TBS_DEV 
   DATAFILE 'tbs_dev.dat' SIZE 40M 
   ONLINE; 
CREATE USER DEV IDENTIFIED BY DEV DEFAULT TABLESPACE TBS_DEV QUOTA 100M ON TBS_DEV;

/********   ******/

-- CATEGORY

CREATE TABLE DEV.CATEGORY ( 
ID   NUMBER
, NAME    VARCHAR2(40)
, PARENT_ID   NUMBER
, CONSTRAINT    CATEGORY_PK PRIMARY KEY (ID ) );