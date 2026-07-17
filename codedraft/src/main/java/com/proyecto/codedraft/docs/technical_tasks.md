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
  "tags": ["string"]
}
```

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
| PATCH | `/api/courses/{id}/progress` | Actualizar el progreso de un curso (dispara coach y puntos de experiencia). |
| DELETE | `/api/courses/{id}` | Eliminar un curso. |
| GET | `/api/courses/recommendation` | Obtener el siguiente curso recomendado. |
| GET | `/api/courses/suggested` | Obtener cursos sugeridos del catálogo según el perfil. |

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
| TT-017 | Desarrollar endpoint para actualizar el progreso (`PATCH /api/courses/{id}/progress`) | BE | Alta | TT-006, TT-011 | Valida rango 0-100, actualiza `courses.json` y cambia el estado a "Completado" automáticamente cuando progreso = 100. |
| TT-018 | Agregar control de progreso (slider/input) en la interfaz | FE | Alta | TT-009 | El control restringe valores entre 0 y 100 y envía la actualización al backend. |
| TT-019 | Conectar la actualización de progreso con el coach y los puntos de experiencia | BE | Media | TT-017 | Cada actualización exitosa invoca al servicio de IA para el mensaje motivador y suma puntos de experiencia al perfil. |

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

| ID | Tarea | Capa | Prioridad | Dependencias | Criterios de aceptación |
|----|-------|------|-----------|---------------|---------------------------|
| TT-024 | Crear el catálogo estático de cursos sugeridos (`catalog.json` con tags) | BE | Media | — | `catalog.json` contiene al menos 10 cursos con tags de interés. |
| TT-025 | Desarrollar endpoint de cursos sugeridos (`GET /api/courses/suggested`) | BE | Alta | TT-002, TT-024 | Filtra `catalog.json` según los intereses del perfil; retorna una lista general si no hay coincidencias. |
| TT-026 | Construir la vista de cursos sugeridos | FE | Alta | — | Muestra tarjetas con los cursos sugeridos tras completar el perfil. |
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
| TT-037 | Sumar puntos de experiencia en cada actualización de progreso | BE | Media | TT-019, TT-036 | Cada actualización exitosa incrementa `experiencePoints` en +5 (configurable) y persiste el cambio en `profile.json`. |
| TT-038 | Desarrollar endpoint para consultar los puntos (`GET /api/profile/points`) | BE | Media | TT-036 | Retorna el total acumulado de puntos de experiencia. |
| TT-039 | Mostrar los puntos de experiencia en el header/dashboard | FE | Media | TT-038 | El total de puntos se actualiza visualmente tras cada actualización de progreso. |

---

## 4. Resumen

- **Total de tareas técnicas:** 39 (TT-001 a TT-039), a nivel de funcionalidad (endpoint o componente de interfaz completo).
- **Orden de construcción:** HU-001 → HU-004 → HU-005 → HU-010 → HU-006 → HU-007 → HU-008 → HU-009 → HU-011 → HU-002/HU-003 → HU-012 → HU-013 → HU-014 → HU-015.
- **Sin base de datos:** toda la persistencia se resuelve leyendo/escribiendo `profile.json`, `courses.json` y `catalog.json` con Jackson desde el backend de Spring Boot.


---
## 5 Tareas completadas 
HU-001 → HU-004 → HU-005 → HU-010 → HU-006