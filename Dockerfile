# 使用 OpenJDK 17 作為基底映像
FROM openjdk:17-jdk-slim

# 設定工作目錄
WORKDIR /app

# 複製 Maven 打包後的 JAR 檔案
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# 指定應用啟動指令
ENTRYPOINT ["java", "-jar", "app.jar"]
