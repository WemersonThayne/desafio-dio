FROM openjdk:17.0-jdk-slim

ENV ENVIRONMENT=""
ENV JVM_OPTIONS=""

VOLUME /tmp
COPY ./target/*.jar /kotlin-estudos.jar

ENTRYPOINT ["tini", "--"]

CMD ["/bin/sh", "-c", "exec java ${JVM_OPTIONS} -Dspring.profiles.active=${ENVIRONMENT} -jar /kotlin-estudos.jar"]