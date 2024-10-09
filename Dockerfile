FROM openjdk:21-jdk-slim as build
# Establecemos el directorio de trabajo para el proceso de construcción
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Para depurar y verificar si el .jar se genera correctamente
RUN ls -l /app/target/

# Fase de ejecución
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copiamos el archivo JAR generado
COPY --from=build /app/target/*.jar /app/upbdocs.jar

# Ejecutamos la aplicación
ENTRYPOINT ["java", "-jar", "/app/upbdocs.jar"]
EXPOSE 8080