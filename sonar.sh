echo usage: ./sonar.sh oh-common[ test]

./setenv.sh

JAVA_HOME="C:/jdk/jdk-17"
MODULE_NAME=$1
TEST=$2

SONAR_OPTS="$SONAR_URL -Dsonar.projectKey=$MODULE_NAME"
SONAR_OPTS="$SONAR_OPTS -Dsonar.projectName=$MODULE_NAME"

if [ "$TEST" == "test" ]; then
    ./gradlew -S $MODULE_NAME:test $MODULE_NAME:jacocoTestReport $MODULE_NAME:sonar $SONAR_OPTS
else
    ./gradlew -S -x test $MODULE_NAME:sonar $SONAR_OPTS
fi
