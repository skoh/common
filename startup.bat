@REM usage: startup oh-common

call setenv

set MODULE_NAME=%1
set /P VERSION_TEMP=<%MODULE_NAME%\gradle.properties
set VERSION=%VERSION_TEMP:~8%

%JAVA_PATH%java %JAVA_OPTS% -jar %MODULE_NAME%/build/libs/%MODULE_NAME%-%VERSION%.war
