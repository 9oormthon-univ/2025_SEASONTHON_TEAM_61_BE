# --- 1단계: 빌드(Build) 스테이지 ---
# Gradle과 JDK 17이 포함된 이미지를 빌드 환경으로 사용합니다. 'build'라는 별명을 붙여줍니다.
FROM gradle:8.5-jdk17-alpine AS build

# 컨테이너 내부의 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# Gradle 관련 파일들을 먼저 복사하여 Docker의 레이어 캐시를 활용합니다.
# 이렇게 하면 소스코드 변경 시 매번 의존성을 새로 받지 않아 빌드 속도가 향상됩니다.
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# gradlew 스크립트에 실행 권한을 부여합니다.
RUN chmod +x ./gradlew

# 의존성을 먼저 다운로드합니다.
RUN ./gradlew dependencies

# 나머지 소스 코드를 복사합니다.
COPY src ./src

# 테스트를 제외하고 애플리케이션을 빌드하여 실행 가능한 JAR 파일을 생성합니다.
RUN ./gradlew build -x test


# --- 2단계: 실행(Runtime) 스테이지 ---
# 실제 애플리케이션을 실행할 최종 이미지의 베이스를 설정합니다.
# JDK가 아닌 JRE를 사용하여 이미지 크기를 최소화합니다.
FROM eclipse-temurin:17-jre-jammy

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 빌드 스테이지('build')에서 생성된 JAR 파일을 복사해옵니다.
# JAR 파일 이름을 app.jar로 통일하여 실행을 용이하게 합니다.
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션이 8080 포트를 사용함을 명시합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 실행할 명령어를 정의합니다.
# java -jar app.jar 명령으로 Spring Boot 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]