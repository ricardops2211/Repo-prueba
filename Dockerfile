# syntax=docker/dockerfile:1
# ^ Habilita sintaxis moderna de Dockerfile (útil con BuildKit)

# --------
# ARGs: variables que puedes pasar al build con --build-arg
# --------
ARG PYTHON_VERSION=3.12

# Imagen base (ligera, Alpine)
FROM python:${PYTHON_VERSION}-alpine

# --------
# Labels (metadata estándar OCI)
# Nota: GITHUB_REPOSITORY se setea en GitHub Actions; en local puedes omitirlo.
# --------
LABEL org.opencontainers.image.title="demo-container" \
      org.opencontainers.image.description="Built by GitHub Actions" \
      org.opencontainers.image.source="https://github.com/${GITHUB_REPOSITORY}"

# --------
# Variables de entorno recomendadas para Python
# --------
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

# --------
# Instalar utilidades mínimas
# - tini: init process (maneja señales correctamente)
# - ca-certificates: certificados TLS (buena práctica)
# --------
RUN apk add --no-cache tini ca-certificates

# --------
# Crear usuario no-root (mejor seguridad)
# --------
RUN addgroup -S app && adduser -S app -G app

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# --------
# Copiar tu contenido
# - En este ejemplo, se espera que exista una carpeta demo-site/ en el repo.
# - Si tu contenido real está en "dist/" o "build/" cambia la ruta.
# --------
COPY demo-site/ /app/

# Exponer puerto (documentación; igual debes mapear con -p)
EXPOSE 8000

# --------
# Healthcheck:
# - Docker lo usa para saber si el contenedor “está sano”.
# - Aquí NO usamos curl/wget; usamos Python que ya está.
# --------
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD python -c "import urllib.request; urllib.request.urlopen('http://127.0.0.1:8000', timeout=2).read()" || exit 1

# Cambiamos a usuario no-root
USER app

# --------
# ENTRYPOINT con tini
# - tini se encarga de manejar SIGTERM/SIGINT correctamente (para CI/CD y prod)
# --------
ENTRYPOINT ["/sbin/tini", "--"]

# Comando por defecto del contenedor
CMD ["python", "-m", "http.server", "8000", "--directory", "/app"]

