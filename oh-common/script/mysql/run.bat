SET BIN_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin

SET DB_HOST=localhost
SET DB_PORT=3306
SET DB_DBA_USER=root
SET DB_DBA_PW=1234567890
SET DB_USER=common
SET DB_PW=1234567890
SET DB_NAME=ds_common
SET CHARACTER_SET=utf8

REM Create database, user by dba
"%BIN_PATH%\mysql" -h %DB_HOST% -P %DB_PORT% -u %DB_DBA_USER% -p%DB_DBA_PW% ^
--default-character-set=%CHARACTER_SET% <run_dba.sql >run_dba.log 2>&1

REM Create table, function, procedure by user
"%BIN_PATH%\mysql" -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PW% %DB_NAME% ^
--default-character-set=%CHARACTER_SET% <run_user.sql >run_user.log 2>&1
