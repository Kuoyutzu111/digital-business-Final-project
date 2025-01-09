# 使用 Maven 官方镜像进行构建阶段
FROM maven:3.8.8-eclipse-temurin-17 AS build

WORKDIR /app

# 仅复制必要文件，避免包含不需要的内容
COPY pom.xml /app/pom.xml
COPY src /app/src

# 使用 Maven 构建项目
RUN mvn clean package -DskipTests

# 使用轻量级 JDK 镜像运行应用程序
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# 从构建阶段复制生成的 JAR 文件
COPY --from=build /app/target/*.jar app.jar

# 设置容器启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
