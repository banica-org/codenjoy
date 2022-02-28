FROM maven:3.8.1-openjdk-11-slim AS codenjoy


WORKDIR app
COPY ./CodingDojo .

RUN mvn -f ./games/engine/pom.xml install
RUN mvn -f ./games/kata/pom.xml install
RUN mvn -f ./messages/pom.xml install
RUN mvn -f ./common/pom.xml install
RUN mvn -f ./server/pom.xml package -D skipTests -P bomberman

ENV SPRING_PROFILES=postgres,kata,bomberman,debug
ENV CODENJOY_CONTEXT=codenjoy-contest
ENV POSTGRES_HOST=host.docker.internal
ENV GAMESERVER_HOST=host.docker.internal
ENV DOJOSERVER_HOST=host.docker.internal

EXPOSE 8080
EXPOSE 9090
EXPOSE 9092

CMD ["java", "-jar", "./server/target/codenjoy-contest.jar", "--spring.profiles.active=${SPRING_PROFILES}", "--context=/${CODENJOY_CONTEXT}", "--postgres.host=${POSTGRES_HOST}", "--gameserver.host=${GAMESERVER_HOST}", "--dojoserver.host=${DOJOSERVER_HOST}"]