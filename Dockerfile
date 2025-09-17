FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

FROM eclipse-temurin:21-jre AS extract
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /app/target/bankcards-*.jar ./bankcards.jar
RUN java -Djarmode=layertools -jar bankcards.jar extract

FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y curl
WORKDIR /app
VOLUME /tmp
COPY --from=extract /app/dependencies/ ./
COPY --from=extract /app/spring-boot-loader/ ./
COPY --from=extract /app/snapshot-dependencies/ ./
COPY --from=extract /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]