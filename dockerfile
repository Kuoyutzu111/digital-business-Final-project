# Step 1: Use Maven to build the application
FROM maven:3.8.8-eclipse-temurin-17 AS build

WORKDIR /app

# 仅复制必要的文件
COPY paper_system/pom.xml /app/pom.xml
COPY paper_system/src /app/src

# 下载依赖并打包项目
RUN mvn clean package -DskipTests

# Step 2: Use a lightweight JDK image to运行应用程序
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# 从构建阶段复制生成的 JAR 文件
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
