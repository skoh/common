chcp 65001
@REM set JAVA_PATH=C:\jdk\jdk-17\bin\
@REM set _JAVA_OPTIONS=-Xmx512M

set JAVA_OPTS=-server
set JAVA_OPTS=%JAVA_OPTS% -Xms256m
set JAVA_OPTS=%JAVA_OPTS% -Xmx2g
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
set JAVA_OPTS=%JAVA_OPTS% -Duser.timezone=Asia/Seoul
@REM set JAVA_OPTS=%JAVA_OPTS% -Duser.language=ko
@REM set JAVA_OPTS=%JAVA_OPTS% --add-opens=java.base/java.io=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% -Dspring.profiles.active=local
@REM set JAVA_OPTS=%JAVA_OPTS% -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005

@REM set JAVA_OPTS=%JAVA_OPTS% -Dnetworkaddress.cache.ttl=0
@REM set JAVA_OPTS=%JAVA_OPTS% -Dnetworkaddress.cache.negative.ttl=0
@REM set JAVA_OPTS=%JAVA_OPTS% -Djava.net.preferIPv4Stack=true

@REM set JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote
@REM set JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote.port=1099
@REM set JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote.local.only=false
@REM set JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote.authenticate=false
@REM set JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote.ssl=false

@REM set JAVA_OPTS=%JAVA_OPTS% -verbose:gc
@REM set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCDetails
@REM set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCDateStamps
@REM set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCTimeStamps
@REM set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintHeapAtGC
@REM set JAVA_OPTS=%JAVA_OPTS% -XX:+DisableExplicitGC
@REM set JAVA_OPTS=%JAVA_OPTS% -XX:+UseGCLogFileRotation
@REM set JAVA_OPTS=%JAVA_OPTS% -XX:GCLogFileSize=50M
@REM set JAVA_OPTS=%JAVA_OPTS% -XX:NumberOfGCLogFiles=10
@REM set JAVA_OPTS=%JAVA_OPTS% -Xloggc:logs\gc.log

@REM set JAVA_OPTS=%JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError
@REM set JAVA_OPTS=%JAVA_OPTS% -XX:OnOutOfMemoryError="kill -3 %p"
@REM set JAVA_OPTS=%JAVA_OPTS% -XX:HeapDumpPath=logs
@REM set JAVA_OPTS=%JAVA_OPTS% -XX:-OmitStackTraceInFastThrow

@REM set BUILD_PATH=build\libs
@REM set PROJECT_PATH=edublock
@REM set TODAY=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%
@REM set TOTIME=%TIME: =0%
@REM set TOTIME=%TOTIME:~0,2%%TIME:~3,2%%TIME:~6,2%
