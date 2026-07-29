# CodeCraftHub — Contexto para Bolt.new (Frontend)

Este documento reúne todo el contexto necesario para generar el frontend de **CodeCraftHub** en Bolt.new, conectado a un backend ya construido en Spring Boot.

---

## 1. ¿Qué es CodeCraftHub?

CodeCraftHub es una plataforma web que ayuda a desarrolladores autodidactas a organizar, priorizar y hacer seguimiento de los cursos que desean estudiar. No ofrece cursos: funciona como un asistente personal que ayuda a decidir qué estudiar primero y a mantener el seguimiento del aprendizaje.

## 2. ¿Qué problemas resuelve?

- No saber qué curso continuar entre varios registrados.
- No saber cuál tiene mayor prioridad.
- No tener claridad de cuánto se ha avanzado.
- No tener visibilidad de qué cursos siguen pendientes.
- No saber cuál curso debería terminarse primero.

## 3. Público objetivo

- Desarrolladores autodidactas.
- Estudiantes de Ingeniería de Sistemas.
- Programadores junior.
- Personas que toman cursos en múltiples plataformas (Coursera, Udemy, etc.).
- Profesionales que quieren organizar su aprendizaje continuo.

## 4. ¿Qué diferencia a CodeCraftHub de otros sistemas?

- No es una plataforma de cursos (como Coursera/Udemy): es un organizador y priorizador de cursos que el usuario ya tiene o quiere tomar en otras plataformas.
- Prioriza automáticamente qué estudiar primero según prioridad, estado y fecha objetivo.
- Incluye un coach virtual ("Mentor Backend") que motiva y acompaña el progreso.
- Sistema de puntos de experiencia (gamificación ligera) al actualizar el progreso.

---

## 5. Stack tecnológico

### Frontend (lo que se construirá en Bolt.new)
- HTML
- CSS
- JavaScript
- Interfaz generada con Bolt.new

### Backend (ya implementado, consumir vía API REST)
- Spring Boot
- Spring Web
- Jackson (lectura/escritura de JSON)
- **Sin base de datos:** toda la persistencia se realiza en archivos JSON (`profile.json`, `courses.json`, `catalog.json`) en el servidor.

> Nota: existe un módulo de IA (Python/Flask) para el coach virtual y perfil de aprendizaje, pero **su backend aún no está desarrollado**. Las funcionalidades relacionadas (mensajes motivadores del coach, perfil de aprendizaje, personaje "Mentor Backend") no deben implementarse en este frontend todavía.

---

## 6. Funcionalidades del sistema (para construir en el frontend)

1. **Registro de perfil de usuario** — Formulario inicial donde el usuario ingresa su rol profesional, carrera e intereses tecnológicos (selección múltiple). Al guardar exitosamente, redirige al dashboard. Valida en cliente que rol y carrera no estén vacíos.

2. **Registro de un nuevo curso** — Formulario para crear un curso con nombre, descripción, estado, prioridad, fecha objetivo y progreso. Estado por defecto "No iniciado" y progreso por defecto 0. El curso creado aparece de inmediato en la lista.

3. **Listado de cursos registrados** — Vista de tabla/lista que muestra nombre, estado, prioridad, fecha objetivo y progreso de cada curso. Muestra un mensaje cuando la lista está vacía o si el backend no responde.

4. **Cambio de estado del curso** — Selector editable (No iniciado / En curso / Completado) directamente desde la lista, sin recargar la página.

5. **Actualización de prioridad** — Selector editable (Alta / Media / Baja) desde la lista, con guardado inmediato.

6. **Actualización de fecha objetivo** — Selector de fecha (datepicker) editable desde la lista, con validación de formato.

7. **Actualización de progreso** — Control tipo slider/input (rango 0–100) que envía la actualización al backend; si el progreso llega a 100 el curso pasa automáticamente a "Completado". Cada actualización exitosa suma puntos de experiencia (ver funcionalidad 10).

8. **Eliminación de un curso** — Botón de eliminar con diálogo de confirmación; el curso desaparece de la lista al confirmar.

9. **Recomendación del siguiente curso a estudiar** — Tarjeta en el dashboard que muestra el curso recomendado según prioridad, estado y fecha objetivo, o un mensaje de "sin cursos pendientes" si no hay ninguno.

10. **Cursos sugeridos personalizados** — Tras completar el perfil, se muestra una lista de tarjetas con cursos sugeridos del catálogo, ordenados por relevancia (score) según el rol, carrera e intereses del usuario.

11. **Puntos de experiencia** — El header/dashboard muestra el total de puntos de experiencia acumulados, actualizándose visualmente tras cada actualización de progreso.

> **Fuera de alcance por ahora:** mensajes motivadores del coach, perfil de aprendizaje generado por IA y el personaje "Mentor Backend" (historias HU-012, HU-013 y HU-014) — su backend todavía no está desarrollado, por lo que el frontend no debe intentar consumir esos endpoints.

---

## 7. Modelo de datos (archivos JSON)

Sin base de datos: cada recurso se lee/escribe con Jackson desde un archivo JSON en el servidor.

### `profile.json` — Perfil de usuario (objeto único)

```json
{
  "rol": "string",
  "carrera": "string",
  "intereses": ["string"],
  "experiencePoints": 0
}
```

### `courses.json` — Cursos registrados (arreglo de objetos)

```json
{
  "id": "string (UUID)",
  "name": "string",
  "description": "string",
  "status": "NO_INICIADO | EN_CURSO | COMPLETADO",
  "priority": "ALTA | MEDIA | BAJA",
  "targetDate": "yyyy-MM-dd",
  "progress": 0
}
```

### `catalog.json` — Catálogo de cursos sugeridos (arreglo estático/semilla)

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "roles": ["string"],
  "careers": ["string"],
  "tags": ["string"]
}
```

Cada curso del catálogo puede asociarse a uno o varios roles profesionales (`roles`), carreras (`careers`) y etiquetas tecnológicas (`tags`). Estos campos alimentan el algoritmo de puntuación usado por `/api/courses/suggested`.

**Algoritmo de puntuación (recomendaciones/sugeridos):**

| Criterio | Puntos | Comparación |
|----------|--------|-------------|
| Rol coincide | +3 | `profile.rol` ∈ `catalog[i].roles` |
| Carrera coincide | +1 | `profile.carrera` ∈ `catalog[i].careers` |
| Cada interés coincidente | +2 | `profile.intereses` ∩ `catalog[i].tags` |

Los cursos se ordenan de mayor a menor puntaje. Si ningún curso obtiene puntaje > 0, se retorna el catálogo completo ordenado alfabéticamente.

---

## 8. Endpoints del backend (organizados por módulo)

Base URL sugerida: `http://localhost:8080` (ajustar según el entorno real del backend).

### Módulo: Perfil de usuario

#### `POST /api/profile` — Registrar el perfil del usuario

**Request:**
```json
{
  "rol": "Backend Developer",
  "carrera": "Ingeniería de Sistemas",
  "intereses": ["Java", "Spring Boot", "APIs REST"]
}
```

**Response 201 (éxito):**
```json
{
  "rol": "Backend Developer",
  "carrera": "Ingeniería de Sistemas",
  "intereses": ["Java", "Spring Boot", "APIs REST"],
  "experiencePoints": 0
}
```

**Response 400 (error de validación):**
```json
{
  "error": "El campo 'rol' es obligatorio."
}
```

---

#### `GET /api/profile` — Consultar el perfil del usuario

**Response 200:**
```json
{
  "rol": "Backend Developer",
  "carrera": "Ingeniería de Sistemas",
  "intereses": ["Java", "Spring Boot", "APIs REST"],
  "experiencePoints": 15
}
```

**Response 404 (perfil aún no creado):**
```json
{
  "error": "El perfil no ha sido registrado."
}
```

---

#### `GET /api/profile/points` — Consultar los puntos de experiencia

**Response 200:**
```json
{
  "experiencePoints": 15
}
```

---

### Módulo: Cursos

#### `POST /api/courses` — Registrar un nuevo curso

**Request:**
```json
{
  "name": "Spring Boot desde cero",
  "description": "Curso de fundamentos de Spring Boot y Spring Web.",
  "status": "NO_INICIADO",
  "priority": "ALTA",
  "targetDate": "2026-09-30",
  "progress": 0
}
```

**Response 201 (éxito):**
```json
{
  "id": "3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234",
  "name": "Spring Boot desde cero",
  "description": "Curso de fundamentos de Spring Boot y Spring Web.",
  "status": "NO_INICIADO",
  "priority": "ALTA",
  "targetDate": "2026-09-30",
  "progress": 0
}
```

**Response 400 (error de validación):**
```json
{
  "error": "El campo 'name' es obligatorio."
}
```

---

#### `GET /api/courses` — Listar todos los cursos registrados

**Response 200:**
```json
[
  {
    "id": "3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234",
    "name": "Spring Boot desde cero",
    "description": "Curso de fundamentos de Spring Boot y Spring Web.",
    "status": "EN_CURSO",
    "priority": "ALTA",
    "targetDate": "2026-09-30",
    "progress": 40
  },
  {
    "id": "8a1b2c3d-4e5f-6789-abcd-ef0123456789",
    "name": "JavaScript Moderno",
    "description": "ES6+, async/await y manipulación del DOM.",
    "status": "NO_INICIADO",
    "priority": "MEDIA",
    "targetDate": "2026-11-15",
    "progress": 0
  }
]
```

Si no hay cursos: `[]`.

---

#### `PATCH /api/courses/{id}/status` — Actualizar el estado de un curso

**Request:**
```json
{
  "status": "EN_CURSO"
}
```

**Response 200:**
```json
{
  "id": "3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234",
  "name": "Spring Boot desde cero",
  "description": "Curso de fundamentos de Spring Boot y Spring Web.",
  "status": "EN_CURSO",
  "priority": "ALTA",
  "targetDate": "2026-09-30",
  "progress": 40
}
```

**Response 404:**
```json
{
  "error": "Curso no encontrado."
}
```

---

#### `PATCH /api/courses/{id}/priority` — Actualizar la prioridad de un curso

**Request:**
```json
{
  "priority": "MEDIA"
}
```

**Response 200:**
```json
{
  "id": "3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234",
  "name": "Spring Boot desde cero",
  "description": "Curso de fundamentos de Spring Boot y Spring Web.",
  "status": "EN_CURSO",
  "priority": "MEDIA",
  "targetDate": "2026-09-30",
  "progress": 40
}
```

---

#### `PATCH /api/courses/{id}/target-date` — Actualizar la fecha objetivo de un curso

**Request:**
```json
{
  "targetDate": "2026-12-01"
}
```

**Response 200:**
```json
{
  "id": "3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234",
  "name": "Spring Boot desde cero",
  "description": "Curso de fundamentos de Spring Boot y Spring Web.",
  "status": "EN_CURSO",
  "priority": "MEDIA",
  "targetDate": "2026-12-01",
  "progress": 40
}
```

---

#### `PATCH /api/courses/{id}/progress` — Actualizar el progreso de un curso

**Request:**
```json
{
  "progress": 100
}
```

**Response 200 (progreso 100 → estado cambia automáticamente a COMPLETADO, y se suman puntos de experiencia al perfil):**
```json
{
  "id": "3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234",
  "name": "Spring Boot desde cero",
  "description": "Curso de fundamentos de Spring Boot y Spring Web.",
  "status": "COMPLETADO",
  "priority": "MEDIA",
  "targetDate": "2026-12-01",
  "progress": 100
}
```

**Response 400 (fuera de rango):**
```json
{
  "error": "El progreso debe estar entre 0 y 100."
}
```

> Nota: esta actualización incrementa `profile.experiencePoints` (+5 por defecto) en el backend. Para reflejar el nuevo total en el header, volver a consultar `GET /api/profile/points` tras una actualización exitosa.

---

#### `DELETE /api/courses/{id}` — Eliminar un curso

**Response 200 / 204 (éxito):** sin cuerpo, o:
```json
{
  "message": "Curso eliminado correctamente."
}
```

**Response 404:**
```json
{
  "error": "Curso no encontrado."
}
```

---

#### `GET /api/courses/{id}` — Obtener el detalle de un curso por ID

**Response 200:**
```json
{
  "id": "3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234",
  "name": "Spring Boot desde cero",
  "description": "Curso de fundamentos de Spring Boot y Spring Web.",
  "status": "EN_CURSO",
  "priority": "ALTA",
  "targetDate": "2026-09-30",
  "progress": 40
}
```

**Response 404:**
```json
{
  "message": "No se encontro un curso con id 3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234"
}
```

---

#### `PATCH /api/courses/{id}/update` — Actualizar múltiples campos de un curso (status, priority, progress, targetDate)

**Request (todos los campos son opcionales):**
```json
{
  "status": "EN_CURSO",
  "priority": "ALTA",
  "progress": 50,
  "targetDate": "2026-10-30"
}
```

**Response 200:**
```json
{
  "id": "3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234",
  "name": "Spring Boot desde cero",
  "description": "Curso de fundamentos de Spring Boot y Spring Web.",
  "status": "EN_CURSO",
  "priority": "ALTA",
  "targetDate": "2026-10-30",
  "progress": 50
}
```

**Response 400 (error de validación):**
```json
{
  "message": "Un curso en curso debe tener un progreso entre 1% y 99%"
}
```

**Response 404:**
```json
{
  "message": "No se encontro un curso con id 3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234"
}
```

---

#### `GET /api/courses/search` — Buscar cursos por filtros (nombre, status, priority, rango de progreso)

**Query Parameters (todos opcionales):**
- `name`: filtro por nombre (búsqueda parcial, case-insensitive)
- `status`: filtro por estado exacto (NO_INICIADO, EN_CURSO, COMPLETADO)
- `priority`: filtro por prioridad exacta (ALTA, MEDIA, BAJA)
- `minProgress`: progreso mínimo (inclusive)
- `maxProgress`: progreso máximo (inclusive)

**Ejemplo:** `GET /api/courses/search?name=java&status=EN_CURSO&minProgress=20&maxProgress=80`

**Response 200:**
```json
[
  {
    "id": "3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234",
    "name": "Java Avanzado",
    "description": "Curso avanzado de Java",
    "status": "EN_CURSO",
    "priority": "ALTA",
    "targetDate": "2026-09-30",
    "progress": 40
  }
]
```

Si no hay resultados: `[]`.

---

#### `GET /api/courses/stats` — Obtener estadísticas de los cursos

**Response 200:**
```json
{
  "totalCourses": 10,
  "notStarted": 3,
  "inProgress": 5,
  "completed": 2,
  "highPriority": 4,
  "mediumPriority": 4,
  "lowPriority": 2,
  "averageProgress": 45.5
}
```

---

### Módulo: Recomendaciones y catálogo

#### `GET /api/courses/recommendation` — Obtener el siguiente curso recomendado

**Response 200 (con curso recomendado):**
```json
{
  "id": "3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234",
  "name": "Spring Boot desde cero",
  "description": "Curso de fundamentos de Spring Boot y Spring Web.",
  "status": "EN_CURSO",
  "priority": "ALTA",
  "targetDate": "2026-09-30",
  "progress": 40
}
```

**Response 200 (sin cursos pendientes):**
```json
{
  "message": "No hay cursos pendientes."
}
```

---

#### `GET /api/courses/suggested` — Obtener cursos sugeridos del catálogo según el perfil

**Response 200:**
```json
[
  {
    "id": "cat-001",
    "name": "Spring Boot Avanzado",
    "description": "Microservicios y APIs REST con Spring Boot.",
    "roles": ["Backend Developer"],
    "careers": ["Ingeniería de Sistemas"],
    "tags": ["Java", "Spring Boot"],
    "score": 5
  },
  {
    "id": "cat-002",
    "name": "Introducción a Docker",
    "description": "Contenedores para despliegue de aplicaciones.",
    "roles": ["Backend Developer", "DevOps"],
    "careers": ["Ingeniería de Sistemas"],
    "tags": ["DevOps"],
    "score": 4
  }
]
```

> Si ningún curso obtiene puntaje (`score > 0`), la respuesta contiene el catálogo completo ordenado alfabéticamente (con `score: 0`).

---

## 9. Resumen para Bolt.new

Construir un frontend en HTML/CSS/JavaScript que incluya:

- Pantalla de **onboarding/perfil** (formulario rol, carrera, intereses).
- **Dashboard** con: tarjeta de curso recomendado, puntos de experiencia, lista de cursos sugeridos.
- Vista de **gestión de cursos**: formulario de registro + tabla/lista con acciones inline (cambiar estado, prioridad, fecha objetivo, progreso, eliminar).
- Consumir únicamente los endpoints Spring Boot documentados en la sección 8 (no implementar coach virtual ni mensajes de IA todavía).
