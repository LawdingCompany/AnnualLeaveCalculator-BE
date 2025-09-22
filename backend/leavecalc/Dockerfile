# 빌드 스테이지
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app

# 1) 캐시 최적화: gradle wrapper/세팅 먼저 복사
COPY gradlew gradle gradle.* ./
RUN chmod +x gradlew

# 2) 소스 복사
COPY . .

# 3) 빌드 (plain.jar 안 만들어지니까 app.jar 하나만 나옴)
RUN ./gradlew clean bootJar -x test --no-daemon

# 실행 스테이지
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드 스테이지에서 나온 app.jar 복사
COPY --from=build /app/build/libs/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
