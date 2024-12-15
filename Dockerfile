# Usa una imagen de Java 11 (puedes usar otra versión si es necesario)
FROM openjdk:11-jre-slim

# Define el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR desde tu máquina local al contenedor
COPY target/ejercicio-1.0-SNAPSHOT.jar /app/ejercicio-1.0-SNAPSHOT.jar

# Exponer el puerto en el que se ejecutará la aplicación
EXPOSE 7000

# Comando para ejecutar el archivo JAR con la clase principal correcta
CMD ["java", "-cp", "ejercicio-1.0-SNAPSHOT.jar", "java.Domain.Server.App"]