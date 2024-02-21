echo usage: build oh-common

call setenv

set MODULE_NAME=%1

gradlew -S -x test %MODULE_NAME%:clean %MODULE_NAME%:build
