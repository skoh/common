@REM usage: publish oh-common

call setenv

set MODULE_NAME=%1

gradlew -S %MODULE_NAME%:publish
