-- SHOW VARIABLES LIKE 'validate_password%';
-- SET GLOBAL validate_password_policy=LOW;

DROP USER IF EXISTS common;
CREATE USER common IDENTIFIED BY '1234567890';
GRANT ALL PRIVILEGES ON ds_common.* TO common;

-- DROP USER IF EXISTS common@localhost;
-- CREATE USER common@localhost IDENTIFIED BY '1234567890';
-- GRANT ALL PRIVILEGES ON ds_common.* TO common@localhost;
