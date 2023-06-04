#!/bin/bash

# Zulu OpenJDK 15를 기반으로하는 기본 이미지
FROM azul/zulu-openjdk:15.0.10

# jar 파일 위치를 변수로 설정
ARG JAR_FILE=build/libs/server-1.0.0-SNAPSHOT.jar

# 환경변수 설정
ENV CUSTOM_NAME default

# jar 파일을 컨테이너 내부에 server-app 이름으로 복사
COPY ${JAR_FILE} server-app.jar

# 웹서버 포트를 외부에 노출
EXPOSE 8080

# 도커가 server-1.0.0-SNAPSHOT.jar 를 실행할 명령어
# @Win10: java "-Dfile.encoding=UTF-8" -jar .\build\libs\server-1.0.0-SNAPSHOT.jar
CMD ["java", "-Dfile.encoding=UTF-8", "-Dtest.customName=${CUSTOM_NAME}", "-jar", "server-app.jar"]

