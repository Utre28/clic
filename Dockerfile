FROM eclipse-temurin:17-jdk

# Ruta del archivo .jar generado por Maven
ARG JAR_FILE=target/*.jar

# Copiar el .jar dentro del contenedor con nombre app.jar
COPY ${JAR_FILE} app.jar

# Puerto que usará la aplicación Spring Boot
EXPOSE 8080

# Comando que ejecuta tu app
ENTRYPOINT ["java", "-jar", "/app.jar"]
