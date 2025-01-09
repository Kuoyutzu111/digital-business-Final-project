# 使用 Railway 默认基础镜像
FROM ghcr.io/railwayapp/nixpacks:ubuntu

# 安装 Maven 和其他依赖
RUN apt-get update && apt-get install -y maven

# 设置工作目录
WORKDIR /app

# 复制项目文件
COPY . /app/

# 构建 Maven 项目
RUN mvn clean package -DskipTests

# 使用轻量级 JDK 镜像运行应用程序
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# 复制生成的 JAR 文件
COPY --from=0 /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
