# Multi-stage build para Docker optimizado

# Etapa 1: Construcción con Maven y JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Establecer directorio de trabajo
WORKDIR /build

# Copiar el Maven Wrapper primero para cachear dependencias
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descargar dependencias (se cacheará si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación y crear el JAR ejecutable
RUN ./mvnw clean package -DskipTests -B

# Etapa 2: Ejecución con JRE 21 (imagen más ligera)
FROM eclipse-temurin:21-jre-alpine

# Crear usuario sin privilegios para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=builder /build/target/codedraft-0.0.1-SNAPSHOT.jar app.jar

# Cambiar propietario de los archivos al usuario sin privilegios
RUN chown -R spring:spring /app

# Cambiar al usuario sin privilegios
USER spring:spring


# Exponer el puerto
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
