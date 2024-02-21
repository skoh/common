#!/bin/bash

BIN_PATH=/usr/bin

DB_HOST=localhost
DB_PORT=3306
DB_DBA_USER=root
DB_DBA_PW=1234567890
DB_USER=common
DB_PW=1234567890
DB_NAME=ds_common
CHARACTER_SET=utf8

# Create database, user by dba
sudo "${BIN_PATH}/mysql" -h ${DB_HOST} -P ${DB_PORT} -u ${DB_DBA_USER} -p${DB_DBA_PW} \
--default-character-set=${CHARACTER_SET} <run_dba.sql >run_dba.log 2>&1

# Create table, function, procedure by user
"${BIN_PATH}/mysql" -h ${DB_HOST} -P ${DB_PORT} -u ${DB_USER} -p${DB_PW} ${DB_NAME} \
--default-character-set=${CHARACTER_SET} <run_user.sql >run_user.log 2>&1