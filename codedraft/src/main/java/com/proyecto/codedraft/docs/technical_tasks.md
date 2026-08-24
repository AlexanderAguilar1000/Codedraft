# CodeCraftHub - Desglose de Tareas Técnicas

Basado en el [Documento de Historias de Usuario](./user_stories.md) y en el stack definido en [vision.md](./vision.md):

- **Frontend:** HTML, CSS, JavaScript (interfaz generada con Bolt.new o Bolt.diy).
- **Backend:** Spring Boot + Spring Web + Jackson, **sin base de datos** — persistencia en archivos JSON.
- **IA:** Python + Flask (perfil de aprendizaje, recomendaciones y mensajes del coach).

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
// POST /learning-profile -> response
{
  "summary": "string",
  "recommendedFocus": ["string"]
}
```

```json
// POST /coach-message -> response
{
  "character": "Mentor Backend",
  "message": "string"
}
```

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
| POST | `/api/study-sessions` | Registrar una sesión de estudio: avanza el progreso del curso y otorga experiencia al perfil según la duración. |

### Servicio de IA (Flask)

| Método | Endpoint | Descripción |
|--------|----------|--------------|
| GET | `/health` | Verificar disponibilidad del servicio. |
| POST | `/learning-profile` | Generar el perfil de aprendizaje a partir de rol, carrera e intereses. |
| POST | `/coach-message` | Generar el mensaje motivador del coach según el progreso. |

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
| TT-028 | Inicializar el servicio Flask con endpoint de salud (`GET /health`) | IA | Media | — | Responde 200 cuando el servicio está disponible. |
| TT-029 | Desarrollar endpoint Flask para generar el perfil de aprendizaje (`POST /learning-profile`) | IA | Media | TT-028 | Recibe rol, carrera e intereses y retorna un perfil de aprendizaje en JSON. |
| TT-030 | Integrar la llamada desde Spring Boot al servicio Flask | BE | Media | TT-002, TT-029 | Al guardar el perfil se invoca `/learning-profile`; si el servicio de IA no responde, el registro del perfil no se bloquea. |

---

### HU-013 Recibir mensajes motivadores del coach

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-031 | Desarrollar endpoint Flask para el mensaje motivador (`POST /coach-message`) | IA | Media | TT-028 | Recibe el progreso actualizado y retorna un mensaje breve en JSON. |
| TT-032 | Integrar la llamada al coach desde el endpoint de progreso | BE | Media | TT-017, TT-031 | La respuesta de `PATCH /api/courses/{id}/progress` incluye el mensaje del coach cuando el servicio de IA responde correctamente. |
| TT-033 | Mostrar el mensaje del coach en la interfaz tras actualizar el progreso | FE | Media | TT-018, TT-032 | Aparece un mensaje/toast motivador inmediatamente después de guardar el progreso. |

---

### HU-014 Interactuar con el personaje del coach

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-034 | Definir la identidad visual del personaje "Mentor Backend" (nombre y avatar) | FE | Baja | — | Recurso gráfico y nombre disponibles como assets del proyecto. |
| TT-035 | Aplicar la identidad del personaje en los mensajes del coach | FE | Baja | TT-033, TT-034 | Todos los mensajes muestran el mismo nombre/avatar; si el recurso gráfico falla, el mensaje se muestra igual en texto. |

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

- **Total de tareas técnicas:** 44 (TT-001 a TT-044), a nivel de funcionalidad (endpoint o componente de interfaz completo). TT-019 y TT-037 quedaron reemplazadas por HU-016.
- **Orden de construcción:** HU-001 → HU-004 → HU-005 → HU-010 → HU-006 → HU-007 → HU-008 → HU-009 → HU-011 → HU-002/HU-003 → HU-012 → HU-013 → HU-014 → HU-015 → HU-016.
- **Sin base de datos:** toda la persistencia se resuelve leyendo/escribiendo `profile.json`, `courses.json`, `catalog.json` y `study_sessions.json` con Jackson desde el backend de Spring Boot.


---
## 5 Tareas completadas 
HU-001 → HU-004 → HU-005 → HU-010 → HU-006 -> HU-007 Actualizar la fecha objetivo de un curso , HU-008 Actualizar el progreso de un curso , H09 Eliminar un curso 
Hu-011 , HU-002 Recomendaciones personalizadas + HU-003 Cursos sugeridos ,  HU-015 , HU-016 Registrar sesión de estudio y ganar experiencia según el tiempo dedicado

Las demas historias de usuario faltan es para la IA 


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
