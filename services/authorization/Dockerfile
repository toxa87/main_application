# Используем официальную Java-версию
FROM openjdk:21-jdk-slim

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем собранный .jar (можно поменять на target/*.jar)
COPY target/authorization-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт (опционально)
EXPOSE 8080

# Точка входа — запуск приложения с параметрами
ENTRYPOINT ["java", "-jar", "app.jar"]