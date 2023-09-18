CREATE DATABASE springbootapp;
CREATE USER 'spring'@'%' IDENTIFIED BY 'pass1234';
CREATE USER 'springinit'@'%' IDENTIFIED BY 'pass1234';
GRANT SELECT, CREATE, INSERT, UPDATE, DELETE ON springbootapp.* TO 'spring'@'%';
GRANT ALL ON springbootapp.* TO 'springinit'@'%';
FLUSH PRIVILEGES;