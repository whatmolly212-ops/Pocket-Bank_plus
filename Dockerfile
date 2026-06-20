# 第一阶段：利用 Maven 镜像在云端直接编译代码
FROM maven:3.8.8-eclipse-temurin-17-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

# 第二阶段：把编译好的 jar 包拉出来运行
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
