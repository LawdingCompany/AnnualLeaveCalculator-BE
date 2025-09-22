# 빌드 스테이지
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app

# 1) 캐시 최적화: 먼저 래퍼/세팅 파일만 복사
COPY gradlew gradle gradle.* ./
RUN chmod +x gradlew

# 2) 소스 복사
COPY . .

# 3) 빌드
RUN ./gradlew clean build -x test --no-daemon

# 실행 스테이지
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
