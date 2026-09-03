# CodeCraftHub - Desglose de Tareas Técnicas

Basado en el [Documento de Historias de Usuario](./user_stories.md) y en el stack definido en [vision.md](./vision.md):

- **Frontend:** HTML, CSS, JavaScript (interfaz generada con Bolt.new o Bolt.diy).
- **Backend:** Spring Boot + Spring Web + Jackson, **sin base de datos** — persistencia en archivos JSON.
- **IA:** Python + Flask + Groq (feedback del Mentor sobre sesiones de estudio; perfil de aprendizaje pendiente). Las recomendaciones de cursos **no** son responsabilidad de la IA — las calcula el algoritmo de puntuación de Spring Boot (sección 6).

**Leyenda de capa:** BE = Backend (Spring Boot) · FE = Frontend (HTML/CSS/JS) · IA = Servicio Python/Flask

---

## 1. Modelo de datos (archivos JSON)

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

> Cada curso puede asociarse a uno o varios roles profesionales (`roles`), carreras (`careers`) y etiquetas tecnológicas (`tags`). Estos campos se utilizan en el algoritmo de puntuación para las recomendaciones personalizadas (ver sección HU-002/HU-003 y [Logica_recomendacion_cursos_CodeCraftHub.md](./Logica_recomendacion_cursos_CodeCraftHub.md)).

### `study_sessions.json` — Sesiones de estudio registradas (arreglo de objetos)

```json
{
  "id": "string (UUID)",
  "courseId": "string",
  "date": "yyyy-MM-dd",
  "duration": "-1 | 1 | 2",
  "notes": "string",
  "progressAdded": 0,
  "experiencePoints": 0,
  "createdAt": "yyyy-MM-ddTHH:mm:ss"
}
```

> `duration` es el código que envía el combobox del frontend: `-1` = menos de 1 hora, `1` = 1 hora, `2` = más de 1 hora. El backend lo traduce a `progressAdded` (5, 10 o 15 puntos) y lo suma al `progress` del curso. `experiencePoints` es el **total acumulado del perfil** (`profile.experiencePoints`) justo después de registrar la sesión, no el delta ganado. Ver sección 7.

### Estructuras del servicio de IA (no persistidas, solo request/response)

```json
// POST /learning-profile -> response (PENDIENTE, ver sección 8.4 — no implementado todavía)
{
  "summary": "string",
  "recommendedFocus": ["string"]
}
```

```json
// POST /coach-message -> request (implementado)
{
  "user": { "rol": "string", "carrera": "string", "intereses": ["string"] },
  "course": { "name": "string", "description": "string" },
  "progress": 0,
  "studySession": { "durationMinutes": 0, "notes": "string" }
}
```

```json
// POST /coach-message -> response (implementado; reemplaza el shape antiguo {character, message})
{
  "character": "Mentor CodeDraft",
  "valid": true,
  "message": "string",
  "whyItMatters": "string | null",
  "realWorldUse": "string | null",
  "challenge": "string | null"
}
```

> El personaje ya no es "Mentor Backend" fijo: es "Mentor CodeDraft", un mentor único que adapta el ángulo de su explicación al `rol` del estudiante (backend, frontend, DevOps, etc.), no solo a backend. Ver sección 8.

---

## 2. Endpoints a desarrollar

### Backend (Spring Boot)

| Método | Endpoint | Descripción |
|--------|----------|--------------|
| POST | `/api/profile` | Registrar el perfil del usuario (rol, carrera, intereses). |
| GET | `/api/profile` | Consultar el perfil del usuario. |
| GET | `/api/profile/points` | Consultar los puntos de experiencia acumulados. |
| POST | `/api/courses` | Registrar un nuevo curso. |
| GET | `/api/courses` | Listar todos los cursos registrados. |
| PATCH | `/api/courses/{id}/status` | Actualizar el estado de un curso. |
| PATCH | `/api/courses/{id}/priority` | Actualizar la prioridad de un curso. |
| PATCH | `/api/courses/{id}/target-date` | Actualizar la fecha objetivo de un curso. |
| PATCH | `/api/courses/{id}/progress` | Corrección manual del progreso de un curso. **No** otorga puntos de experiencia (ver HU-016). |
| PATCH | `/api/courses/{id}/update` | Actualizar múltiples campos de un curso (status, priority, progress, targetDate) en una sola llamada. |
| DELETE | `/api/courses/{id}` | Eliminar un curso. |
| GET | `/api/courses/{id}` | Obtener el detalle de un curso por ID. |
| GET | `/api/courses/search` | Buscar cursos por filtros (nombre, status, priority, rango de progreso). |
| GET | `/api/courses/stats` | Obtener estadísticas de los cursos (total, por estado, por prioridad, promedio de progreso). |
| GET | `/api/courses/recommendation` | Obtener el siguiente curso recomendado. |
| GET | `/api/courses/suggested` | Obtener cursos sugeridos del catálogo según el perfil. |
| POST | `/api/study-sessions` | Registrar una sesión de estudio: avanza el progreso del curso, otorga experiencia al perfil según la duración, y devuelve embebido el feedback del Mentor IA (`mentorCharacter`, `mentorValid`, `mentorMessage`, `mentorWhyItMatters`, `mentorRealWorldUse`, `mentorChallenge`). Si el servicio de IA no responde, la sesión se guarda igual y esos campos quedan en `null`. |

> No existe un endpoint público adicional en Spring Boot para pedir el feedback del mentor por separado: viaja embebido en la respuesta de `POST /api/study-sessions` (decisión de diseño, ver sección 8.2). `IA_Message/controller/IA_Controller.java` sigue vacío/sin uso por esa razón — queda reservado para cuando se implemente `/learning-profile` del lado de Spring Boot.

### Servicio de IA (Flask) — proyecto separado `ai-service/` (Python + Flask + Groq)

| Método | Endpoint | Estado | Descripción |
|--------|----------|--------|--------------|
| GET | `/health` | ✅ Implementado | Verificar disponibilidad del servicio. |
| POST | `/coach-message` | ✅ Implementado | Analiza `notes`, valida si describe un aprendizaje real, y si es válido genera feedback + importancia + aplicación real + reto, adaptado al `rol` del estudiante. Ver sección 8. |
| POST | `/learning-profile` | ⏳ Pendiente | Generar el perfil de aprendizaje a partir de rol, carrera e intereses. Analizado (costo/beneficio) pero no implementado — ver sección 8.4. |

---

## 3. Tareas técnicas por historia de usuario

> Cada tarea agrupa lo necesario para completar una funcionalidad (validación + persistencia en JSON + respuesta), sin desglosar en pasos de repositorio o pruebas unitarias.

### HU-001 Completar perfil de usuario

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-001 | Maquetar formulario de perfil (rol, carrera, intereses) | FE | Alta | — | El formulario valida en el cliente que rol y carrera no estén vacíos. |
| TT-002 | Desarrollar endpoint para registrar el perfil (`POST /api/profile`) | BE | Alta | — | Valida campos obligatorios, escribe `profile.json` y responde 201; 400 si faltan datos. |
| TT-003 | Desarrollar endpoint para consultar el perfil (`GET /api/profile`) | BE | Alta | TT-002 | Retorna el perfil almacenado o 404 si aún no existe. |
| TT-004 | Conectar el formulario con el backend y redirigir al dashboard | FE | Alta | TT-001, TT-002 | Al guardar con éxito redirige al dashboard; muestra el error del backend si falla. |

---

### HU-004 Registrar un nuevo curso

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-005 | Maquetar formulario de registro de curso | FE | Alta | — | Captura nombre, descripción, estado, prioridad, fecha objetivo y progreso, con valores por defecto (estado "No iniciado", progreso 0). |
| TT-006 | Desarrollar endpoint para registrar curso (`POST /api/courses`) | BE | Alta | — | Valida nombre obligatorio y progreso 0-100, genera un id único y agrega el curso a `courses.json`; 201 en éxito, 400 si es inválido. |
| TT-007 | Conectar el formulario con el backend y refrescar la lista | FE | Alta | TT-005, TT-006 | El curso creado aparece de inmediato en la lista; los errores se muestran en el formulario. |

---

### HU-005 Consultar la lista de cursos registrados

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-008 | Desarrollar endpoint para listar cursos (`GET /api/courses`) | BE | Alta | TT-006 | Retorna el contenido completo de `courses.json` (arreglo vacío si no hay cursos). |
| TT-009 | Construir la vista de lista/tabla de cursos | FE | Alta | — | Muestra nombre, estado, prioridad, fecha objetivo y progreso de cada curso; indica cuando la lista está vacía. |
| TT-010 | Conectar la vista con el endpoint de listado | FE | Alta | TT-008, TT-009 | La lista se carga al iniciar la vista; muestra un mensaje si el backend no responde. |

---

### HU-010 Clasificar el estado del curso

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-011 | Desarrollar endpoint para actualizar el estado (`PATCH /api/courses/{id}/status`) | BE | Alta | TT-006 | Valida que el valor sea uno de los 3 estados permitidos, actualiza `courses.json`; 404 si el curso no existe. |
| TT-012 | Agregar selector de estado editable en la interfaz | FE | Alta | TT-009 | El usuario cambia el estado desde la lista y el cambio se refleja sin recargar la página. |

---

### HU-006 Actualizar la prioridad de un curso

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-013 | Desarrollar endpoint para actualizar la prioridad (`PATCH /api/courses/{id}/priority`) | BE | Media | TT-006 | Valida el valor (Alta/Media/Baja) y actualiza `courses.json`. |
| TT-014 | Agregar selector de prioridad editable en la interfaz | FE | Media | TT-009 | El cambio de prioridad se guarda y refleja sin recargar la página. |

---

### HU-007 Actualizar la fecha objetivo de un curso

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-015 | Desarrollar endpoint para actualizar la fecha objetivo (`PATCH /api/courses/{id}/target-date`) | BE | Media | TT-006 | Valida el formato de fecha y actualiza `courses.json`. |
| TT-016 | Agregar selector de fecha editable en la interfaz | FE | Media | TT-009 | El datepicker restringe el formato válido y el cambio se refleja sin recargar. |

---

### HU-008 Actualizar el progreso de un curso

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-017 | Desarrollar endpoint para actualizar el progreso (`PATCH /api/courses/{id}/progress`) | BE | Alta | TT-006, TT-011 | Valida rango 0-100, actualiza `courses.json` y cambia el estado a "Completado" automáticamente cuando progreso = 100. **No otorga experiencia**: es una corrección manual (ver HU-016). |
| TT-018 | Agregar control de progreso (slider/input) en la interfaz | FE | Alta | TT-009 | El control restringe valores entre 0 y 100 y envía la actualización al backend. |
| ~~TT-019~~ | ~~Conectar la actualización de progreso con el coach y los puntos de experiencia~~ | BE | Media | TT-017 | **Reemplazada por HU-016.** La asignación de experiencia se movió al registro de sesiones de estudio para evitar que el progreso se otorgue dos veces (una por la sesión y otra por este PATCH). La integración con el coach de IA sigue pendiente (HU-013). |

---

### HU-009 Eliminar un curso

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-020 | Desarrollar endpoint para eliminar curso (`DELETE /api/courses/{id}`) | BE | Media | TT-006 | Elimina el curso de `courses.json`; 404 si no existe. |
| TT-021 | Agregar botón de eliminar con confirmación en la lista | FE | Media | TT-009, TT-020 | Requiere confirmación antes de eliminar; el curso desaparece de la lista al confirmar. |

---

### HU-011 Recibir recomendación del siguiente curso a estudiar

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-022 | Desarrollar endpoint de recomendación (`GET /api/courses/recommendation`) | BE | Alta | TT-006, TT-011, TT-013, TT-015 | Ordena los cursos pendientes por prioridad, estado y fecha objetivo, y retorna el primero; responde con mensaje vacío si no hay pendientes. |
| TT-023 | Mostrar tarjeta de "próximo curso recomendado" en el dashboard | FE | Alta | TT-022 | Muestra el curso recomendado o el mensaje "sin cursos pendientes". |

---

### HU-002 Recomendaciones personalizadas + HU-003 Cursos sugeridos

> **Lógica de recomendación:** Se aplica un algoritmo de puntuación basado en el perfil del usuario (ver [Logica_recomendacion_cursos_CodeCraftHub.md](./Logica_recomendacion_cursos_CodeCraftHub.md)).
>
> | Criterio | Puntos |
> |----------|--------|
> | Coincidencia de **rol** (`profile.rol` ∈ `catalog.roles`) | **+3** |
> | Coincidencia de **carrera** (`profile.carrera` ∈ `catalog.careers`) | **+1** |
> | Cada **interés** coincidente (`profile.intereses` ∩ `catalog.tags`) | **+2** por cada uno |
>
> Los cursos se ordenan de **mayor a menor puntaje**. Si ningún curso obtiene puntaje, se retorna el catálogo completo ordenado alfabéticamente.

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-024 | Crear el catálogo estático de cursos sugeridos (`catalog.json` con `roles`, `careers` y `tags`) | BE | Media | — | `catalog.json` contiene al menos 10 cursos; cada curso incluye los campos `roles`, `careers` y `tags`. |
| TT-025 | Desarrollar endpoint de cursos sugeridos con algoritmo de puntuación (`GET /api/courses/suggested`) | BE | Alta | TT-002, TT-024 | Calcula el puntaje de cada curso del catálogo según rol (+3), carrera (+1) e intereses (+2 cada uno) del perfil; retorna la lista ordenada de mayor a menor puntaje incluyendo el score; si ningún curso puntúa, retorna el catálogo completo ordenado alfabéticamente. |
| TT-026 | Construir la vista de cursos sugeridos | FE | Alta | — | Muestra tarjetas con los cursos sugeridos tras completar el perfil; cada tarjeta puede mostrar el puntaje de relevancia. |
| TT-027 | Conectar la vista de sugeridos con el endpoint | FE | Alta | TT-025, TT-026 | La lista se carga automáticamente al finalizar el registro del perfil. |

---

### HU-012 Generar perfil de aprendizaje (coach)

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-028 | Inicializar el servicio Flask con endpoint de salud (`GET /health`) | IA | Media | — | ✅ Responde 200 cuando el servicio está disponible. |
| TT-029 | Desarrollar endpoint Flask para generar el perfil de aprendizaje (`POST /learning-profile`) | IA | Media | TT-028 | ⏳ Pendiente. Recibe rol, carrera e intereses y retorna un perfil de aprendizaje en JSON. Se analizó el costo/beneficio (ver sección 8.4): bajo costo (se llamaría una sola vez, al registrar el perfil) pero valor decorativo, sin lugar aún en la UI para mostrarlo — se decidió priorizar primero `/coach-message`. |
| TT-030 | Integrar la llamada desde Spring Boot al servicio Flask | BE | Media | TT-002, TT-029 | ⏳ Pendiente (depende de TT-029). Al guardar el perfil se invocaría `/learning-profile`; si el servicio de IA no responde, el registro del perfil no debe bloquearse (mismo patrón fail-open que `CoachAiClient`, ver sección 8.3). |

---

### HU-013 Recibir mensajes motivadores del coach

> El diseño original de esta historia (coach enganchado a `PATCH /api/courses/{id}/progress`, con un mensaje corto tipo toast) cambió durante la implementación: el feedback de IA quedó ligado al registro de la **sesión de estudio** (`POST /api/study-sessions`, HU-016), no a la corrección manual de progreso, y el contenido pasó de "mensaje motivador breve" a un análisis real de lo que el estudiante escribió (`notes`), con validación semántica y contenido estructurado. Ver sección 8 para el detalle completo.

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| ~~TT-031~~ | ~~Desarrollar endpoint Flask para el mensaje motivador (`POST /coach-message`)~~ | IA | Media | TT-028 | **Reemplazada por TT-045.** El endpoint sí recibe el nombre `/coach-message`, pero ya no recibe "el progreso actualizado" para devolver un mensaje corto: recibe perfil + curso + progreso + `notes`, valida si `notes` describe un aprendizaje real, y solo si es válido genera feedback + importancia + aplicación real + reto. |
| ~~TT-032~~ | ~~Integrar la llamada al coach desde el endpoint de progreso~~ | BE | Media | TT-017, TT-031 | **Reemplazada por TT-047.** El punto de integración ya no es `PATCH /api/courses/{id}/progress`: es `POST /api/study-sessions` (HU-016), porque ahí es donde existe `notes` para analizar. |
| ~~TT-033~~ | ~~Mostrar el mensaje del coach en la interfaz tras actualizar el progreso~~ | FE | Media | TT-018, TT-032 | **Reemplazada por TT-048.** No es un toast: el feedback se muestra dentro del mismo modal "Registrar progreso", reemplazando el formulario, con secciones separadas (mensaje, por qué importa, aplicación real, reto). |
| TT-045 | Desarrollar el servicio Flask completo de `POST /coach-message` (`ai-service/app.py`): validación semántica de `notes`, generación de feedback estructurado, reintento si el LLM devuelve JSON inválido | IA | Alta | TT-028 | ✅ Responde `valid:false` con mensaje pidiendo mejor descripción si `notes` no describe un aprendizaje (vacío, sin sentido, no relacionado); responde `valid:true` con `message`/`whyItMatters`/`realWorldUse`/`challenge` si sí lo describe, aunque sea breve o general; reintenta una vez si el LLM no devuelve JSON válido antes de fallar con 502. |
| TT-046 | Desarrollar el cliente HTTP en Spring Boot hacia el servicio de IA (`CoachAiClient`, paquete `IA_Message`) | BE | Alta | TT-045 | ✅ Usa `RestClient` con timeouts de conexión/lectura configurables (`app.ai-service.*` en `application.properties`); nunca propaga excepciones — cualquier fallo de Flask (timeout, conexión, error HTTP) se loguea y devuelve `Optional.empty()`. |
| TT-047 | Integrar `CoachAiClient` en `StudySessionService.registerSession()` y persistir/exponer el feedback del mentor | BE | Alta | TT-041, TT-046 | ✅ Después de calcular progreso y XP, se arma el contexto (perfil + curso + progreso + notas) y se llama a `CoachAiClient`; el resultado (o `null` si la IA falló) se persiste en `StudySession` (`study_sessions.json`) y se expone en `StudySessionResponse` — el registro de la sesión nunca falla por culpa de la IA. |
| TT-048 | Mostrar el feedback del mentor en el modal "Registrar progreso" del frontend | FE | Alta | TT-044, TT-047 | ✅ Al recibir la respuesta de `POST /api/study-sessions`, si trae `mentorCharacter`, el modal reemplaza el formulario por el feedback (o solo el mensaje, si `mentorValid=false`) con un botón "Entendido"; si no trae `mentorCharacter` (IA no disponible), el modal se cierra igual que antes de este cambio. |

---

### HU-014 Interactuar con el personaje del coach

> El diseño original planteaba un personaje fijo "Mentor Backend" con avatar gráfico. Durante la implementación se decidió (ver conversación de diseño, sección 8.2) que el mentor debe **adaptar el ángulo de su explicación al rol del estudiante** (Backend, Frontend, DevOps, Data, etc.), no hablar siempre desde una óptica backend — por lo que el personaje se renombró a "Mentor CodeDraft" (uno solo, sin variar por rol) y no se construyó un avatar gráfico: la identidad visual es textual + un ícono y una paleta de color (morado) reservados exclusivamente para contenido de IA en la interfaz.

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| ~~TT-034~~ | ~~Definir la identidad visual del personaje "Mentor Backend" (nombre y avatar)~~ | FE | Baja | — | **Reemplazada por TT-049.** No se construyó un avatar gráfico; el personaje final se llama "Mentor CodeDraft" y adapta su enfoque al rol del estudiante en vez de hablar siempre como especialista backend. |
| ~~TT-035~~ | ~~Aplicar la identidad del personaje en los mensajes del coach~~ | FE | Baja | TT-033, TT-034 | **Reemplazada por TT-049.** |
| TT-049 | Aplicar una identidad visual consistente al feedback de IA en el modal de progreso (ícono + acento de color reservado para IA) | FE | Baja | TT-048 | ✅ El bloque `.mentor-feedback` usa el ícono `sparkles` y el acento `--purple` de la paleta existente (distinto de los colores de gamificación y del teal primario), para que el estudiante identifique visualmente que ese contenido lo generó el mentor de IA. |

---

### HU-015 Ganar puntos de experiencia al actualizar el progreso

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-036 | Agregar el campo de puntos de experiencia al perfil | BE | Media | TT-002 | `profile.json` incluye `experiencePoints` inicializado en 0. |
| ~~TT-037~~ | ~~Sumar puntos de experiencia en cada actualización de progreso~~ | BE | Media | TT-036 | **Reemplazada por HU-016.** Ya no se suma un monto fijo (+5) por cada `PATCH /progress`; los puntos ahora dependen de la duración de la sesión de estudio registrada (5, 10 o 15). |
| TT-038 | Desarrollar endpoint para consultar los puntos (`GET /api/profile/points`) | BE | Media | TT-036 | Retorna el total acumulado de puntos de experiencia. |
| TT-039 | Mostrar los puntos de experiencia en el header/dashboard | FE | Media | TT-038 | El total de puntos se actualiza visualmente tras cada actualización de progreso. |

---

### HU-016 Registrar sesión de estudio y ganar experiencia según el tiempo dedicado

> Sustituye el otorgamiento automático de experiencia de HU-008/HU-015. El usuario ya no ingresa un puntaje: registra cuánto estudió (curso, fecha, duración, notas) y el backend calcula el avance del curso y la experiencia ganada en una sola operación atómica. Ver el detalle de la lógica en la sección 7.

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-040 | Modelar el recurso `StudySession` y su repositorio JSON (`study_sessions.json`) en el paquete `progress_student` | BE | Alta | TT-036 | El modelo persiste `id`, `courseId`, `date`, `duration`, `notes`, `progressAdded`, `experiencePoints` y `createdAt`; el repositorio sigue el mismo patrón findAll/saveAll que `CourseRepository`/`ProfileRepository`. |
| TT-041 | Desarrollar endpoint para registrar sesiones de estudio (`POST /api/study-sessions`) | BE | Alta | TT-006, TT-017, TT-040 | Valida `courseId`, `date` y `duration` (`-1`, `1` o `2`); traduce la duración a `progressAdded` (5/10/15) y actualiza el progreso del curso vía `updateProgress`, capando en 100. Responde 404 si el curso no existe y 400 si el curso ya está al 100% o la duración es inválida. |
| TT-042 | Otorgar experiencia al perfil desde el registro de sesión, sin duplicarla en `PATCH /progress` | BE | Alta | TT-019, TT-037, TT-041 | `updateProgress()` ya no suma experiencia (se le quitó la dependencia de `ProfileService`); la única vía para ganar experiencia es `POST /api/study-sessions`, que suma `progressAdded` al perfil y guarda el total resultante como `experiencePoints` de la sesión. |
| TT-043 | Maquetar el modal de registro de sesión de estudio con combobox de duración | FE | Alta | TT-009 | El modal captura curso, fecha, notas y un combobox con las opciones "Menos de 1 hora" (-1), "1 hora" (1) y "Más de 1 hora" (2); no incluye ningún campo de puntaje manual. |
| TT-044 | Conectar el modal con `POST /api/study-sessions` y refrescar progreso/experiencia en la interfaz | FE | Alta | TT-041, TT-043 | Al guardar, la lista de cursos y el contador de experiencia del header se actualizan con los valores devueltos por el backend; los errores (curso completado, duración inválida) se muestran en el modal. |

---

## 4. Resumen

- **Total de tareas técnicas:** 49 (TT-001 a TT-049), a nivel de funcionalidad (endpoint o componente de interfaz completo). TT-019, TT-037, TT-031, TT-032, TT-033, TT-034 y TT-035 quedaron reemplazadas (ver HU-016 y HU-013/HU-014 actualizadas).
- **Orden de construcción:** HU-001 → HU-004 → HU-005 → HU-010 → HU-006 → HU-007 → HU-008 → HU-009 → HU-011 → HU-002/HU-003 → HU-015 → HU-016 → HU-013 (parcial) → HU-014 (parcial) → HU-012 (pendiente).
- **Sin base de datos:** toda la persistencia se resuelve leyendo/escribiendo `profile.json`, `courses.json`, `catalog.json` y `study_sessions.json` con Jackson desde el backend de Spring Boot.
- **Servicio de IA:** vive en un proyecto Python separado (`ai-service/`, Flask + SDK de Groq), no dentro de este repo de Spring Boot. Ver sección 8.


---
## 5 Tareas completadas 
HU-001 → HU-004 → HU-005 → HU-010 → HU-006 -> HU-007 Actualizar la fecha objetivo de un curso , HU-008 Actualizar el progreso de un curso , H09 Eliminar un curso 
Hu-011 , HU-002 Recomendaciones personalizadas + HU-003 Cursos sugeridos ,  HU-015 , HU-016 Registrar sesión de estudio y ganar experiencia según el tiempo dedicado

HU-013 Recibir feedback del coach (implementada con otro diseño al planteado originalmente, ver sección 8) y HU-014 Interactuar con el personaje del coach (implementada sin avatar gráfico) ya están completas.

**Pendiente de IA:** HU-012 Generar perfil de aprendizaje (`/learning-profile`) — analizada (costo/beneficio) pero no implementada todavía, ver sección 8.4.


----
## 6 Lógica de Recomendación de cursos — CATALOG.JSON 

Se implementa un **algoritmo de puntuación** basado en el perfil del usuario, según lo descrito en [Logica_recomendacion_cursos_CodeCraftHub.md](./Logica_recomendacion_cursos_CodeCraftHub.md).

| Criterio | Puntos | Comparación |
|----------|--------|-------------|
| Rol coincide | +3 | `profile.rol` ∈ `catalog[i].roles` |
| Carrera coincide | +1 | `profile.carrera` ∈ `catalog[i].careers` |
| Cada interés coincidente | +2 | `profile.intereses` ∩ `catalog[i].tags` |

**Fallback:** si ningún curso obtiene puntaje > 0, se retorna el catálogo completo ordenado alfabéticamente.


----
## 7 Lógica de sesiones de estudio y experiencia — STUDY_SESSIONS.JSON

Implementado en el paquete `com.proyecto.codedraft.progress_student` (`controller`, `dto`, `model`, `repositorio`, `servicio`), siguiendo la misma arquitectura por capas que `course` y `profile`.

**Motivación:** originalmente `PATCH /api/courses/{id}/progress` recibía el progreso ya calculado por el frontend y, además, otorgaba +5 de experiencia fija en cada llamada. Eso permitía que el usuario "inventara" el progreso y generaba doble conteo de experiencia si el mismo cambio pasaba por dos caminos. Ahora el progreso y la experiencia se derivan **siempre en el backend**, a partir de una sesión de estudio real.

### Flujo de `POST /api/study-sessions`

1. El frontend abre un modal con: curso, fecha, duración (combobox) y notas — **sin** campo de puntaje.
2. El combobox envía un código entero como `duration`: `-1` (menos de 1 hora), `1` (1 hora) o `2` (más de 1 hora).
3. El backend traduce `duration` a `progressAdded`:

   | `duration` | Significado | `progressAdded` |
   |---|---|---|
   | `-1` | Menos de 1 hora | 5 |
   | `1` | 1 hora | 10 |
   | `2` | Más de 1 hora | 15 |

4. Se valida que el curso no esté ya al 100% (`CourseAlreadyCompletedException` si lo está).
5. El nuevo progreso del curso es `min(100, progreso actual + progressAdded)` y se persiste llamando a `CourseService.updateProgress(...)`.
6. Se suma `progressAdded` al perfil vía `ProfileService.addExperiencePoints(...)`; el total resultante se guarda en la sesión como `experiencePoints` (snapshot del perfil, no el delta de esta sesión — un mismo usuario puede tener varios cursos en curso y el perfil acumula experiencia de todos).
7. Se guarda la sesión completa en `study_sessions.json`.

**Camino elegido para evitar el doble conteo (A):** `CourseService.updateProgress()` ya **no** depende de `ProfileService` ni otorga experiencia — quedó como una corrección manual de progreso (p. ej. arrastrar un slider), sin recompensa. La experiencia solo se otorga desde `POST /api/study-sessions`.


----
## 8 Lógica de IA — Mentor CodeDraft (implementado)

Ver el diseño original en [CodeDraft_Logica_IA_MVP.md](./CodeDraft_Logica_IA_MVP.md). Esta sección documenta lo que realmente se construyó, incluyendo los puntos donde la implementación se apartó de ese diseño inicial.

### 8.1 Servicio de IA (`ai-service/`)

Proyecto Python + Flask **separado** de este repo (no es un módulo de Spring Boot). Usa el SDK de **Groq** (no Claude/OpenAI) contra un modelo configurado por variable de entorno (`GROP_MODEL` en `.env`, junto a `GROP_API_KEY`). Expone:

- `GET /health` — chequeo de disponibilidad.
- `POST /coach-message` — único endpoint de negocio implementado (ver 8.2).
- `POST /learning-profile` — **no implementado** (ver 8.4).

### 8.2 Decisión de integración: síncrono y embebido, no un endpoint aparte

El diseño original sugería que el frontend pidiera el mensaje del coach por separado. Se decidió en su lugar que `StudySessionService.registerSession()` (Spring Boot) llame él mismo a Flask **de forma síncrona**, como parte del mismo request de `POST /api/study-sessions`:

```text
Frontend --POST /api/study-sessions--> StudySessionController
                                            |
                                            v
                                 StudySessionService.registerSession()
                                            |
                    guarda sesión, actualiza progreso del curso, suma XP
                                            |
                                            v
                              CoachAiClient.requestCoachMessage(...)
                                            |
                                            v
                          POST http://localhost:5000/coach-message  (Flask)
                                            |
                                            v
                                        Groq (LLM)
                                            |
                                            v
                     Flask responde {valid, message, whyItMatters, realWorldUse, challenge}
                                            |
                                            v
              se guarda en StudySession y se devuelve dentro de StudySessionResponse
```

Por eso **no existe** un endpoint público en Spring Boot para pedir el coach-message por separado — `IA_Message/controller/IA_Controller.java` quedó vacío/sin uso.

### 8.3 Manejo de fallos (fail-open)

`CoachAiClient` (paquete `IA_Message.service`) usa `RestClient` con timeouts configurables (`app.ai-service.connect-timeout-ms` / `read-timeout-ms` en `application.properties`, default 2s/8s) y **nunca propaga una excepción**: cualquier fallo (timeout, conexión rechazada, error HTTP, JSON inesperado) se captura, se loguea como warning, y devuelve `Optional.empty()`. Si eso ocurre, `StudySession` guarda `null` en los 6 campos `mentor*` y la sesión se registra exactamente igual — el estudiante nunca ve un error por culpa de la IA.

### 8.4 `/learning-profile` — analizado, no implementado

Se evaluó explícitamente si construirlo ahora: es barato (solo se llamaría una vez, al registrar el perfil, porque hoy no existe un endpoint de actualización de perfil) y reutilizaría el mismo patrón de `CoachAiClient`, pero es decorativo frente al objetivo de validación del MVP (organizar/priorizar/dar seguimiento a cursos) y **hoy no hay espacio en `profile.js`** para mostrar `summary`/`recommendedFocus`. Se decidió posponerlo.

### 8.5 Diseño del prompt: un solo personaje, adaptado por rol

El diseño original de `CodeDraft_Logica_IA_MVP.md` asumía un personaje fijo, "Mentor Backend", que siempre habla desde una óptica de backend. Esto se descartó en la práctica: el perfil del estudiante (`profile.rol`) puede ser cualquiera de los 9 roles del dropdown del frontend (`Codedraft_Fronted/src/data/mockData.js`: Backend Developer, Frontend Developer, Fullstack Developer, DevOps, Cloud Engineer, Software Architect, Security Engineer, Data Engineer, Mobile Developer), y un Frontend Developer no debería recibir siempre consejos de APIs REST.

Se implementó, en cambio, **un solo personaje ("Mentor CodeDraft") que actúa como experto en la disciplina de `rol`**, no una IA con múltiples personajes (eso sigue explícitamente fuera de alcance del MVP según la sección 14 del documento original). El `SYSTEM_PROMPT` en `ai-service/app.py` incluye una guía de enfoque por rol y dos reglas clave:

1. Interpretar conceptos genéricos/transversales (ej. "endpoint") desde la disciplina de `rol`, no de forma neutral.
2. **Regla de puente entre disciplinas:** cuando `rol` y `curso` pertenecen a disciplinas distintas (ej. un Backend Developer estudiando un curso de Angular), no forzar el concepto dentro de la disciplina de `rol` — explicarlo correctamente en la disciplina real del curso, y usar `rol` solo para conectar ese conocimiento con lo que el estudiante ya domina, sin distorsionar el concepto.

### 8.6 Validación semántica de `notes`

Implementada tal como describe `CodeDraft_Logica_IA_MVP.md` sección 6: notas vacías, sin sentido o no relacionadas con aprendizaje (`"asdfgh"`, `"Hoy fui al gimnasio"`) devuelven `valid:false` con un mensaje pidiendo describir mejor lo aprendido, sin generar `whyItMatters`/`realWorldUse`/`challenge`. Notas válidas pero muy generales (`"Aprendí Spring"`) se aceptan como `valid:true`, invitando a profundizar — no se exige una descripción muy técnica.

### 8.7 Contrato real de `/coach-message` (Spring Boot)

DTOs en `IA_Message/dto/`: `CoachMessageRequest` (con `CoachUserContext`, `CoachCourseContext`, `CoachStudySessionContext`) y `CoachMessageResponse`. La duración real en minutos no existe en el dominio (el frontend solo envía un código `-1/1/2`), así que `StudySessionService` aproxima `durationMinutes` (30/60/90) únicamente como contexto informativo para el prompt — Flask nunca usa ese valor para calcular progreso ni XP, eso sigue siendo responsabilidad exclusiva de Spring Boot (sección 7).

### 8.8 Frontend

`Codedraft_Fronted/src/views/courseModals.js`: al registrar una sesión desde el modal "Registrar progreso", si la respuesta trae `mentorCharacter`, el formulario se reemplaza por el feedback del mentor (mensaje + "por qué importa" + "aplicación real" + "reto práctico", o solo el mensaje si `mentorValid=false`) en vez de cerrar el modal de inmediato. Estilos nuevos en `views.css` (`.mentor-feedback`) usan el acento `--purple` de la paleta existente para diferenciar visualmente el contenido generado por IA.
