FROM openjdk:8-jre-alpine
ARG NAME
RUN mkdir config
COPY $NAME/build/libs/*.war app.war
ENTRYPOINT java -server $JAVA_OPTS -jar app.war
