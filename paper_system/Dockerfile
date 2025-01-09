# 使用 Maven 官方镜像进行构建阶段
FROM maven:3.8.8-eclipse-temurin-17 AS build

# 设置工作目录
WORKDIR /app

# 仅复制 pom.xml，安装依赖
COPY project/pom.xml /app/pom.xml
COPY project/src /app/src


# 运行依赖预加载（可选）
RUN mvn dependency:go-offline

# 复制项目的源代码
COPY src /app/src

# 构建项目
RUN mvn clean package -DskipTests

# 使用轻量级 JDK 镜像运行应用程序
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# 从构建阶段复制生成的 JAR 文件
COPY --from=build /app/target/*.jar app.jar

# 设置容器启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
