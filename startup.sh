echo usage: ./startup.sh oh-common

./setenv.sh

MODULE_NAME=$1
VERSION_TEMP=$(<$MODULE_NAME/gradle.properties)
VERSION=${VERSION_TEMP:8}

${JAVA_PATH}java $JAVA_OPTS -jar $MODULE_NAME/build/libs/$MODULE_NAME-$VERSION.war &
