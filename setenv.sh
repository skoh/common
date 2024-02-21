#JAVA_PATH="C:/jdk/jdk-17/bin/"
export JAVA_PATH
#_JAVA_OPTIONS="-Xmx512M"
export _JAVA_OPTIONS

JAVA_OPTS="-server"
JAVA_OPTS="$JAVA_OPTS -Xms256m"
JAVA_OPTS="$JAVA_OPTS -Xmx2g"
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"
JAVA_OPTS="$JAVA_OPTS -Duser.timezone=Asia/Seoul"
#JAVA_OPTS="$JAVA_OPTS -Duser.language=ko"
#JAVA_OPTS="$JAVA_OPTS --add-opens=java.base/java.io=ALL-UNNAMED"
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=local"
#JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"

#JAVA_OPTS="$JAVA_OPTS -Dnetworkaddress.cache.ttl=0"
#JAVA_OPTS="$JAVA_OPTS -Dnetworkaddress.cache.negative.ttl=0"
#JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"

#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote"
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=1099"
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.local.only=false"
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.ssl=false"

#JAVA_OPTS="$JAVA_OPTS -verbose:gc"
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails"
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDateStamps"
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCTimeStamps"
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintHeapAtGC"
#JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC"
#JAVA_OPTS="$JAVA_OPTS -XX:+UseGCLogFileRotation"
#JAVA_OPTS="$JAVA_OPTS -XX:GCLogFileSize=50M"
#JAVA_OPTS="$JAVA_OPTS -XX:NumberOfGCLogFiles=10"
#JAVA_OPTS="$JAVA_OPTS -Xloggc:logs\gc.log"

#JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
#JAVA_OPTS="$JAVA_OPTS -XX:OnOutOfMemoryError='kill -3 %p'"
#JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=logs"
#JAVA_OPTS="$JAVA_OPTS -XX:-OmitStackTraceInFastThrow"

export JAVA_OPTS
