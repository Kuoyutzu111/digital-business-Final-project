# 使用 Maven 官方镜像进行构建阶段
FROM maven:3.8.8-eclipse-temurin-17 AS build

# 设置工作目录
WORKDIR /app

# 复制项目的 pom.xml 和源代码
COPY pom.xml /app/pom.xml
COPY src /app/src

# 使用 Maven 构建项目
RUN mvn clean package -DskipTests

# 使用轻量级 JDK 镜像运行应用程序
FROM eclipse-temurin:17-jdk-jammy

# 设置工作目录
WORKDIR /app

# 从构建阶段复制生成的 JAR 文件
COPY --from=build /app/target/*.jar app.jar

# 设置容器启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
