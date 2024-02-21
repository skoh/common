echo usage: ./buildMvn.sh oh-common

MODULE_NAME=$1

./mvnw -e -Dmaven.test.skip -am -pl "$MODULE_NAME" clean package
