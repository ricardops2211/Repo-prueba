# syntax=docker/dockerfile:1
# ^ Habilita BuildKit (mejor build/caché)

############################
# Stage 1: build (compilar)
############################
FROM eclipse-temurin:21-jdk-alpine AS build
# ^ JDK para compilar

WORKDIR /app

# Copiamos solo el archivo Java (sin carpetas extra)
COPY Main.java .

# Compilamos a /app/out
RUN mkdir -p out && javac -d out Main.java

############################
# Stage 2: runtime (correr)
############################
FROM eclipse-temurin:21-jre-alpine
# ^ Solo JRE (más liviano)

# Usuario no-root (mejor práctica)
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

# Copiamos los .class compilados
COPY --from=build /app/out /app/out

# Puerto del microservicio
EXPOSE 8080

USER app

# PORT puede venir por env (compose), por defecto 8080 en el código
ENV PORT=8080

# Ejecuta la clase Main
ENTRYPOINT ["java", "-cp", "/app/out", "Main"]
