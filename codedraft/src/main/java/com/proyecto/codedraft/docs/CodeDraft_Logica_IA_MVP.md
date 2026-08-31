# Lógica de IA — CodeDraft

## 1. Objetivo

La IA de CodeDraft funciona como un **Mentor Backend** que acompaña al estudiante autodidacta después de registrar una sesión de estudio.

El objetivo del MVP no es que la IA recomiende cursos ni que controle el progreso o los puntos. Su función es:

1. Analizar qué escribió el estudiante en `notes`.
2. Determinar si las notas representan un aprendizaje válido y relacionado con el curso.
3. Si son válidas:
   - Dar feedback.
   - Explicar por qué lo aprendido es importante.
   - Explicar cómo se utiliza en proyectos reales.
   - Proponer un reto práctico.
4. Si no son válidas:
   - Informar al estudiante que debe describir mejor lo aprendido.
   - No generar feedback ni reto.

---

## 2. Responsabilidades

### Spring Boot

Spring Boot es responsable de:

- Gestionar cursos.
- Gestionar sesiones de estudio.
- Actualizar el progreso del curso.
- Calcular `progressAdded`.
- Calcular y persistir `experiencePoints`.
- Obtener el perfil del usuario.
- Obtener la información del curso.
- Llamar al servicio de IA.
- Decidir qué hacer según `valid`.
- No bloquear el funcionamiento principal si el servicio de IA no está disponible.

### Python + Flask

Flask es el servicio intermediario entre Spring Boot y el LLM.

Responsabilidades:

- Recibir el contexto del estudiante.
- Construir el prompt para el LLM.
- Solicitar una respuesta estructurada.
- Validar que la respuesta del LLM tenga el formato esperado.
- Devolver la respuesta a Spring Boot.

### LLM

El LLM es responsable de interpretar lenguaje natural.

Debe:

- Analizar las `notes`.
- Determinar si describen un aprendizaje relacionado con el curso.
- Devolver `valid: true` o `valid: false`.
- Cuando `valid = true`, generar el feedback del Mentor.
- Cuando `valid = false`, generar un mensaje indicando que las notas deben corregirse.

El LLM **NO** calcula el XP ni modifica el progreso del curso.

---

# 3. Flujo principal

```text
Usuario registra sesión
        |
        v
Spring Boot
        |
        +--> Guarda/actualiza datos del sistema
        |
        +--> Calcula progreso
        |
        +--> Calcula XP
        |
        v
POST /coach-message
        |
        v
Python + Flask
        |
        v
LLM
        |
        v
¿Las notes son válidas?
       /      NO   SÍ
     |     |
     v     v
valid     valid
false     true
     |     |
     |     +--> Feedback
     |     +--> Importancia
     |     +--> Uso real
     |     +--> Reto
     |
     v
Spring Boot
        |
        v
Frontend
```

---

# 4. Datos actuales

## `profile.json`

```json
{
  "rol": "string",
  "carrera": "string",
  "intereses": ["string"],
  "experiencePoints": 0
}
```

`experiencePoints` es responsabilidad exclusiva de Spring Boot.

---

## `courses.json`

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

El `progress` del curso es gestionado por Spring Boot.

---

## `study_sessions.json`

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

`notes` contiene la descripción escrita por el estudiante sobre lo que aprendió.

`duration` es interpretado por Spring Boot para calcular `progressAdded` y XP.

---

# 5. Endpoint `/coach-message`

## Request

```http
POST /coach-message
Content-Type: application/json
```

Request:

```json
{
  "user": {
    "rol": "Backend Developer",
    "carrera": "Ingeniería de Sistemas",
    "intereses": [
      "Java",
      "Spring Boot"
    ]
  },
  "course": {
    "name": "Spring Boot",
    "description": "Desarrollo de APIs REST"
  },
  "progress": 45,
  "studySession": {
    "durationMinutes": 120,
    "notes": "Aprendí a crear endpoints REST utilizando @GetMapping"
  }
}
```

### Información enviada

#### `user`

Contexto del estudiante:

- `rol`
- `carrera`
- `intereses`

Permite que el Mentor adapte el feedback al perfil.

#### `course`

Información del curso que se está estudiando:

- `name`
- `description`

No se envía todo `courses.json`, solamente el curso correspondiente a la sesión.

#### `progress`

Progreso actual del curso después de registrar la sesión.

#### `studySession`

Información de la sesión:

- `durationMinutes`
- `notes`

`notes` es el texto que el LLM debe analizar.

---

# 6. Validación semántica de `notes`

El sistema debe distinguir entre:

### Caso inválido

```text
wwwwww
```

o:

```text
asdfgh
```

o:

```text
Hoy fui al gimnasio.
```

Si el contenido no representa un aprendizaje relacionado con el curso:

```json
{
  "valid": false,
  "character": "Mentor Backend",
  "message": "No pude identificar qué aprendiste en esta sesión. Describe brevemente el concepto o tema que estudiaste.",
  "whyItMatters": null,
  "realWorldUse": null,
  "challenge": null
}
```

---

### Caso válido

```text
Aprendí a crear endpoints REST utilizando @GetMapping.
```

Respuesta:

```json
{
  "valid": true,
  "character": "Mentor Backend",
  "message": "Buen avance. Ya estás aprendiendo a construir APIs REST con Spring Boot.",
  "whyItMatters": "Los endpoints REST permiten que diferentes aplicaciones se comuniquen con tu backend.",
  "realWorldUse": "En CodeDraft podrías utilizarlos para que el frontend consulte y gestione los cursos.",
  "challenge": "Crea un endpoint GET /api/courses/{id} que devuelva un curso por su ID."
}
```

---

### Caso válido pero demasiado general

Ejemplo:

```text
Aprendí Spring.
```

No necesariamente debe rechazarse.

El LLM puede considerarlo válido pero poco específico y generar un feedback que invite al estudiante a profundizar.

La validación debe buscar que exista suficiente contexto para que el Mentor pueda proporcionar una respuesta útil, sin exigir una descripción excesivamente técnica.

---

# 7. Response DTO

La respuesta de `/coach-message` será:

```json
{
  "character": "Mentor Backend",
  "valid": true,
  "message": "string",
  "whyItMatters": "string",
  "realWorldUse": "string",
  "challenge": "string"
}
```

Campos:

| Campo | Descripción |
|---|---|
| `character` | Personaje que proporciona el feedback |
| `valid` | Indica si las `notes` representan un aprendizaje válido |
| `message` | Feedback principal del Mentor |
| `whyItMatters` | Explica por qué el concepto es importante |
| `realWorldUse` | Explica cómo se utiliza en proyectos reales |
| `challenge` | Reto práctico para aplicar lo aprendido |

Cuando `valid = false`, los campos de contenido pueden ser `null`.

---

# 8. XP

La IA no determina los puntos.

El cálculo pertenece a Spring Boot.

```text
Tiempo de estudio
       |
       v
Spring Boot
       |
       v
Regla de XP
       |
       v
experiencePoints
```

Por ejemplo:

```text
< 1 hora -> regla definida por backend
1 hora   -> regla definida por backend
> 1 hora -> regla definida por backend
```

El LLM nunca debe decidir cuánto XP recibe el usuario.

---

# 9. Integración con el progreso

La IA utiliza el progreso como **contexto**, pero no lo modifica.

Ejemplo:

```text
Curso: Spring Boot
Progreso anterior: 40%
Sesión registrada: +5
Progreso actual: 45%
```

Spring Boot calcula:

```text
40% -> 45%
```

Después envía `45` a `/coach-message`.

La IA puede utilizar esa información para personalizar su mensaje:

> "Ya llevas un 45% del curso..."

Pero no puede modificar ese valor.

---

# 10. Manejo de errores

El servicio de IA es una capacidad adicional.

Si Flask o el LLM no responde:

```text
Usuario registra sesión
        |
        v
Spring Boot guarda sesión
        |
        v
Calcula progreso y XP
        |
        v
Intenta llamar a Flask
        |
        v
Servicio IA no disponible
        |
        v
Continúa el flujo normal
```

El fallo de IA no debe impedir que el estudiante registre su sesión.

---

# 11. Endpoint `/learning-profile`

Este endpoint se mantiene como funcionalidad independiente.

## Request

```json
{
  "rol": "Backend Developer",
  "carrera": "Ingeniería de Sistemas",
  "intereses": [
    "Java",
    "Spring Boot"
  ]
}
```

## Response

```json
{
  "summary": "string",
  "recommendedFocus": [
    "string"
  ]
}
```

Su objetivo es generar una interpretación inicial del perfil del estudiante.

No debe utilizarse para calcular recomendaciones de cursos. Las recomendaciones de cursos continúan siendo responsabilidad del algoritmo existente en Spring Boot.

---

# 12. Endpoint `/health`

```http
GET /health
```

Response:

```json
{
  "status": "UP"
}
```

Su única responsabilidad es verificar que el servicio Flask esté disponible.

---

# 13. Arquitectura final del MVP

```text
                    FRONTEND
                        |
                        v
                 ┌─────────────┐
                 │ Spring Boot │
                 └──────┬──────┘
                        |
          ┌─────────────┼─────────────┐
          |             |             |
          v             v             v
       Cursos        Progreso        XP
          |             |             |
          └─────────────┼─────────────┘
                        |
                        v
                Study Session
                        |
                        v
               /coach-message
                        |
                        v
                 ┌───────────┐
                 │   Flask   │
                 └─────┬─────┘
                       |
                       v
                    ┌──────┐
                    │ LLM  │
                    └──┬───┘
                       |
             ┌─────────┴─────────┐
             |                   |
          inválido             válido
             |                   |
             v                   v
       pedir corrección    feedback + reto
```

---

# 14. Alcance del MVP

### Implementar

- `GET /health`
- `POST /learning-profile`
- `POST /coach-message`
- DTOs request/response.
- Integración Spring Boot → Flask.
- Integración Flask → LLM.
- Validación semántica de `notes`.
- Feedback contextual.
- Explicación de importancia.
- Aplicación en proyectos reales.
- Reto práctico.
- Manejo de `valid`.
- Manejo de errores del servicio IA.

### No implementar todavía

- RAG.
- Vector database.
- Agentes.
- Memoria conversacional avanzada.
- Múltiples personajes.
- Evaluación avanzada del estudiante.
- Recomendación de cursos mediante LLM.
- IA para calcular XP.
- IA para modificar el progreso.

---

# 15. Objetivo final del MVP

> **CodeDraft permite que un estudiante autodidacta registre lo que estudió y, a partir de esa sesión, un Mentor IA analiza su aprendizaje, explica la importancia del concepto, muestra cómo aplicarlo en un proyecto real y propone un reto práctico.**

El backend mantiene el control del progreso y la gamificación, mientras que el LLM se encarga de la interpretación y generación del contenido educativo.
