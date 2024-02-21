echo usage: ./build.sh oh-common

./setenv.sh

MODULE_NAME=$1

./gradlew -S -x test "$MODULE_NAME":clean "$MODULE_NAME":build
