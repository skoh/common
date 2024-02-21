@REM usage: sonar oh-common[ test]

call setenv

set JAVA_HOME=C:\jdk\jdk-11
set MODULE_NAME=%1
set TEST=%2

set SONAR_OPTS=%SONAR_URL% -Dsonar.projectKey=%MODULE_NAME%
set SONAR_OPTS=%SONAR_OPTS% -Dsonar.projectName=%MODULE_NAME%

if "%TEST%" == "test" (
    gradlew -S %MODULE_NAME%:test %MODULE_NAME%:jacocoTestReport %MODULE_NAME%:sonar %SONAR_OPTS%
) else (
    gradlew -S -x test %MODULE_NAME%:sonar %SONAR_OPTS%
)
