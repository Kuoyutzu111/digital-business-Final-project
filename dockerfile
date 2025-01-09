# 使用官方 Maven 映像進行構建階段
FROM maven:3.8.6-openjdk-17 AS build

# 設置工作目錄
WORKDIR /app

# 複製項目所有文件到容器內
COPY . .

# 使用 Maven 构建项目，跳过测试以加快构建速度
RUN mvn clean package -DskipTests

# 使用輕量級 JDK 映像運行應用
FROM openjdk:17-jdk-slim

# 設置工作目錄
WORKDIR /app

# 從構建階段復制生成的 JAR 文件到運行階段
COPY --from=build /app/target/paper_system-0.0.1-SNAPSHOT.jar app.jar

# 定義容器啟動命令
ENTRYPOINT ["java", "-jar", "app.jar"]
