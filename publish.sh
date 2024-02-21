echo usage: ./publish.sh oh-common

./setenv.sh

MODULE_NAME=$1

./gradlew -S "$MODULE_NAME":publish
