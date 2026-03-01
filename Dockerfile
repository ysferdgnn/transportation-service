FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew settings.gradle* build.gradle* gradle/ ./
RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies || true

COPY . .

RUN ./gradlew --no-daemon clean bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd -m appuser
USER appuser

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]