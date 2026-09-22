# Imagen oficial slim, versión de Python detectada en .venv/pyvenv.cfg (3.12.3)
FROM python:3.12-slim

# Evita .pyc y fuerza logs sin buffer, útil para ver logs en Render al instante
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

WORKDIR /app

# Copiamos solo el archivo de dependencias primero para cachear esta capa
# y no reinstalar todo cuando solo cambia el código
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copiamos el código de la aplicación
COPY app.py .

# Usuario sin privilegios: la app no necesita correr como root
RUN useradd --create-home --shell /usr/sbin/nologin appuser \
    && chown -R appuser:appuser /app
USER appuser

# Puerto en el que escucha la app (definido en app.run(port=5000)).
# Render inyecta su propia variable PORT en runtime, que el CMD respeta.
EXPOSE 5000

# Gunicorn como servidor WSGI de producción en vez del servidor de desarrollo
# de Flask (app.run(debug=True), inseguro y no apto para producción).
# "app:app" -> módulo app.py, variable Flask "app" definida en él.
CMD ["sh", "-c", "gunicorn --bind 0.0.0.0:${PORT:-5000} --workers 2 app:app"]
