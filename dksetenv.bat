chcp 65001
@REM usage: dksetenv oh-common 8010

@REM set HOST=localhost:8080/
set GROUP=org.oh
set NAME=%1
set VERSION=1.5.0
set FULL_NAME=%HOST%%GROUP%/%NAME%:%VERSION%
set PORT=%2

set JAVA_OPTS=%JAVA_OPTS% -Dspring.profiles.active=demo
