FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app

COPY gradlew gradle gradle.* ./
COPY build.gradle settings.gradle ./

# 의존성 먼저 다운로드 (캐시 레이어)
RUN ./gradlew dependencies --no-daemon || true

COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]