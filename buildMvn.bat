echo usage: buildMvn oh-common

set MODULE_NAME=%1

mvnw -e -Dmaven.test.skip -am -pl %1 clean package
