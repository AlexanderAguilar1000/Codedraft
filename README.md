# 🎓 CodeDraft (CodeCraftHub)

**Asistente personal para que desarrolladores autodidactas organicen, prioricen y den seguimiento a su aprendizaje.**

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?logo=springboot&logoColor=white)
![Python](https://img.shields.io/badge/Python-3.x-3776AB?logo=python&logoColor=white)
![Flask](https://img.shields.io/badge/Flask-000000?logo=flask&logoColor=white)
![Groq](https://img.shields.io/badge/Groq-LLM-F55036)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6+-F7DF1E?logo=javascript&logoColor=black)
![Vite](https://img.shields.io/badge/Vite-646CFF?logo=vite&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Microservices-blue)

📘 Proyecto académico desarrollado como MVP para validar, con usuarios reales, si una herramienta de este tipo aporta valor antes de invertir en funcionalidades más avanzadas.

👨‍💻 Diseño y desarrollo end-to-end (backend, frontend, servicio de IA y arquitectura general) por **[Alexander Aguilar](https://github.com/AlexanderAguilar1000)**.

🚧 Proyecto sin desplegar en producción; usa datos de prueba con fines demostrativos y educativos.

---

## 📑 Contenido

- [Resumen](#-resumen)
- [Problemática](#-problemática)
- [Solución y funcionalidades](#️-solución-y-funcionalidades)
- [Propuesta de valor](#-propuesta-de-valor)
- [Decisiones técnicas destacadas](#-decisiones-técnicas-destacadas)
- [Arquitectura](#️-arquitectura-del-proyecto)
- [Stack tecnológico](#-stack-tecnológico)
- [Estructura de datos](#-estructura-de-datos)
- [API — Endpoints](#-api--endpoints-principales)
- [Ejecución local](#️-ejecución-local)
- [Estado del proyecto](#-estado-del-proyecto)

---

## 📝 Resumen

**CodeDraft** es un sistema de tres servicios (backend en Spring Boot, frontend en JavaScript/Vite y un microservicio de IA en Flask con Groq) que centraliza los cursos que un desarrollador quiere estudiar, calcula automáticamente cuál debería tomar a continuación y acompaña el proceso con feedback generado por IA sobre lo que realmente aprendió, no solo con un contador de progreso.

A diferencia de Coursera o Udemy, **no aloja contenido educativo**: es una capa de organización y coaching sobre los cursos que el usuario ya está tomando en cualquier plataforma.

---

## 🚨 Problemática

Los desarrolladores autodidactas acumulan cursos de distintas plataformas (Udemy, Coursera, YouTube, etc.) sin una forma clara de organizarlos. Con el tiempo se vuelve difícil responder preguntas básicas:

- ¿Qué curso debería continuar?
- ¿Cuál tiene mayor prioridad?
- ¿Cuánto he avanzado realmente?
- ¿Qué cursos siguen pendientes?
- ¿Cuál debería terminar primero?

Esta dispersión genera pérdida de tiempo al decidir qué estudiar, abandono de cursos a medias y ausencia de una guía que oriente el aprendizaje continuo.

---

## 🛠️ Solución y funcionalidades

| Funcionalidad | Qué resuelve |
|---|---|
| 📋 **Gestión de cursos** | CRUD completo con estado (No iniciado / En curso / Completado), prioridad (Alta / Media / Baja), fecha objetivo y progreso (0–100%). |
| 🎯 **Priorización inteligente** | Endpoint que calcula automáticamente el siguiente curso a estudiar combinando prioridad, estado y fecha objetivo — el usuario deja de decidirlo manualmente. |
| 🧭 **Recomendaciones personalizadas** | Algoritmo de puntuación sobre un catálogo de cursos según rol, carrera e intereses del perfil, en vez de una lista genérica igual para todos. |
| ⏱️ **Seguimiento real de avance** | El progreso y la experiencia (XP) se derivan de sesiones de estudio reales (curso, fecha, duración, notas) en lugar de ingresarse manualmente. |
| 🤖 **Mentor de IA ("Mentor CodeDraft")** | Al registrar una sesión, un servicio de IA valida si las notas describen un aprendizaje real y responde con feedback estructurado (mensaje, por qué importa, aplicación real y un reto), adaptado a la disciplina del rol del estudiante. |
| 🎮 **Gamificación ligera** | Puntos de experiencia acumulados por perfil como incentivo de constancia. |

---

## 💡 Propuesta de valor

> CodeDraft ayuda a desarrolladores autodidactas a registrar, priorizar y dar seguimiento a sus cursos para decidir fácilmente qué estudiar primero y mantener constancia en su aprendizaje.

El diferenciador frente a un simple to-do list es el **componente de IA con criterio de dominio**: el mentor no da mensajes motivacionales genéricos, valida semánticamente lo que el estudiante escribió y adapta su explicación a la disciplina del rol (un Backend Developer y un Frontend Developer estudiando el mismo curso reciben ángulos distintos).

---

## 🧠 Decisiones técnicas destacadas

Puntos de diseño pensados deliberadamente durante el desarrollo, más allá de "hacer que funcione":

- **Persistencia sin base de datos, a propósito.** El alcance del MVP no la justificaba: se optó por archivos JSON + Jackson para validar el producto rápido, con un `Repository` por recurso que aísla el resto del código de esa decisión (migrar a una BD real no obligaría a tocar los servicios).
- **Anti-doble-conteo de experiencia.** El diseño inicial otorgaba XP en cada `PATCH` de progreso, lo que permitía inflar el avance manualmente y duplicar puntos si el mismo cambio llegaba por dos caminos. Se resolvió moviendo el otorgamiento de XP exclusivamente al registro de sesiones de estudio, dejando el `PATCH` de progreso como corrección manual sin recompensa.
- **IA con estrategia fail-open.** El servicio de IA vive en un proceso Python separado del backend Java. El cliente HTTP (`CoachAiClient`) nunca propaga una excepción: cualquier timeout, caída o respuesta inválida se captura y degrada a `Optional.empty()`, de modo que una falla de la IA jamás bloquea el flujo principal (registrar la sesión de estudio).
- **Integración síncrona y embebida, no un endpoint extra.** En vez de exponer el feedback del mentor como un endpoint aparte que el frontend debe orquestar, el backend lo resuelve internamente durante `POST /api/study-sessions` y lo devuelve embebido en la misma respuesta — un solo round-trip para el cliente.
- **Alcance de la IA acotado con criterio de costo/beneficio.** Se evaluó explícitamente construir un endpoint de "perfil de aprendizaje" narrativo y se decidió posponerlo por bajo impacto frente al objetivo de validación del MVP (documentado en `docs/technical_tasks.md`), priorizando el coach de sesiones que sí aporta valor recurrente.
- **Un mentor, no una IA multipersonaje.** El diseño original planteaba un personaje fijo con óptica de backend. Se ajustó a un único mentor que adapta el ángulo de su explicación a la disciplina del rol del estudiante, con una regla explícita de "puente entre disciplinas" cuando el rol y el curso no coinciden.

---

## 🏗️ Arquitectura del proyecto

Tres servicios independientes dentro del mismo repositorio, comunicados por HTTP:

```text
Codedraft/
├── Codedraft/            → Backend (Spring Boot) — API REST, sin base de datos
├── Codedraft_Fronted/    → Frontend (HTML/CSS/JS + Vite)
└── ai-service/           → Servicio de IA (Python + Flask + Groq)
```

```text
Frontend (Vite/JS) ──HTTP──▶ Backend (Spring Boot) ──HTTP síncrono──▶ ai-service (Flask) ──▶ Groq (LLM)
                                     │
                                     ▼
                     profile.json · courses.json
                     catalog.json · study_sessions.json
```

- El **backend** no usa base de datos: toda la persistencia se hace leyendo/escribiendo archivos JSON con Jackson.
- El **coach de IA** se invoca de forma síncrona desde el backend al registrar una sesión de estudio (`POST /api/study-sessions`); su respuesta viaja embebida en esa misma llamada.
- Si el servicio de IA no responde, el backend aplica **fail-open**: la sesión se guarda igual y el feedback del mentor queda vacío, sin bloquear al usuario.

---

## 💻 Stack tecnológico

**Backend**
- Java 21 · Spring Boot · Spring Web
- Jackson (lectura/escritura de JSON)
- Persistencia en archivos JSON (sin base de datos)

**Frontend**
- HTML, CSS, JavaScript (vanilla)
- Vite

**Servicio de IA**
- Python · Flask
- Groq SDK (inferencia LLM)
- Pydantic

---

## 📁 Estructura de datos

Toda la información vive en archivos JSON dentro de `Codedraft/data/`:

| Archivo | Contenido |
|---|---|
| `profile.json` | Perfil del usuario (rol, carrera, intereses, XP acumulada) |
| `courses.json` | Cursos registrados por el usuario |
| `catalog.json` | Catálogo semilla de cursos sugeridos (usado por el algoritmo de recomendación) |
| `study_sessions.json` | Historial de sesiones de estudio (progreso, XP y feedback del mentor por sesión) |

---

## 🔌 API — Endpoints principales

### Backend (Spring Boot)

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/profile` | Registrar el perfil del usuario |
| GET | `/api/profile` | Consultar el perfil |
| GET | `/api/profile/points` | Consultar experiencia acumulada |
| POST | `/api/courses` | Registrar un curso |
| GET | `/api/courses` | Listar cursos |
| GET | `/api/courses/search` | Buscar cursos por filtros |
| GET | `/api/courses/stats` | Estadísticas de cursos |
| GET | `/api/courses/recommendation` | Siguiente curso recomendado |
| GET | `/api/courses/suggested` | Cursos sugeridos según el perfil |
| PATCH | `/api/courses/{id}/status` · `/priority` · `/target-date` · `/progress` · `/update` | Actualizar campos de un curso |
| DELETE | `/api/courses/{id}` | Eliminar un curso |
| POST | `/api/study-sessions` | Registrar sesión de estudio (progreso + XP + feedback del mentor) |

### Servicio de IA (Flask)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/health` | Verificar disponibilidad del servicio |
| POST | `/coach-message` | Validar notas de estudio y generar feedback del mentor |

---

## ▶️ Ejecución local

**1. Backend (Spring Boot)** — desde `Codedraft/`:

```bash
./mvnw spring-boot:run
```

Corre por defecto en `http://localhost:8080`.

**2. Servicio de IA (Flask)** — desde `ai-service/`:

```bash
pip install -r requirements.txt
python app.py
```

Requiere un archivo `.env` con `GROP_API_KEY` y `GROP_MODEL` (credenciales de Groq). Corre en `http://localhost:5000`, la URL configurada por defecto en el backend (`app.ai-service.base-url`).

**3. Frontend (Vite)** — desde `Codedraft_Fronted/`:

```bash
npm install
npm run dev
```

> El backend funciona de forma independiente del servicio de IA: si Flask no está corriendo, el registro de cursos y sesiones de estudio sigue funcionando con normalidad, solo sin el feedback del mentor.

---

## 📌 Estado del proyecto

El MVP cubre de forma completa el flujo principal: perfil de usuario, gestión de cursos, recomendaciones personalizadas, priorización inteligente, sesiones de estudio con experiencia y feedback del mentor de IA.

Queda pendiente (evaluado pero no implementado por ahora): generación de un "perfil de aprendizaje" narrativo vía IA (`/learning-profile`), pospuesto por bajo impacto frente al objetivo de validación del MVP.
