# Casos de Prueba - POST /api/courses/registerCurso

**Endpoint:** `POST /api/courses/registerCurso`
**Controlador:** `CursoController.registerCourse`
**Servicio:** `CourseService.registerCourse`
**Requisitos relacionados:** RF-04, RNF-06
**Historia de usuario relacionada:** HU-004

**Precondición general (aplica a todos los casos salvo que se indique lo contrario):**
- El servicio backend está levantado y accesible.
- El archivo `data/courses.json` existe (o el sistema puede crearlo) y no está corrupto.
- No se requiere token/autenticación (el endpoint no implementa seguridad de acceso).

**Estructura del payload (`CourseRequest`):**
```json
{
  "name": "string (obligatorio)",
  "description": "string (opcional)",
  "status": "NO_INICIADO | EN_CURSO | COMPLETADO (opcional, default NO_INICIADO)",
  "priority": "ALTA | MEDIA | BAJA (opcional, default MEDIA)",
  "targetDate": "yyyy-MM-dd (obligatorio, debe ser fecha futura)",
  "progress": "entero 0-100 (opcional, default 0)"
}
```

---

## 1. Casos exitosos

### TC-EXI-001
- **Nombre:** Registrar curso con todos los campos válidos e informados
- **Objetivo:** Verificar que un curso se registra correctamente cuando todos los campos son válidos.
- **Precondiciones:** No existe un curso con el mismo nombre.
- **Datos de entrada:**
```json
{ "name": "Spring Boot Avanzado", "description": "Curso de profundización backend",
  "status": "NO_INICIADO", "priority": "ALTA", "targetDate": "2026-12-01", "progress": 0 }
```
- **Pasos:** 1) Enviar POST con el payload. 2) Revisar código de estado. 3) Revisar cuerpo de respuesta. 4) Verificar persistencia en `courses.json`.
- **Resultado esperado:** `201 CREATED`; body con `id` generado (UUID), y los mismos datos enviados; el curso queda persistido.
- **Prioridad:** Alta

### TC-EXI-002
- **Nombre:** Registrar curso sin `status` ni `priority` (usar valores por defecto)
- **Objetivo:** Verificar que se aplican los defaults `NO_INICIADO` y `MEDIA` cuando no se informan.
- **Precondiciones:** No existe un curso con el mismo nombre.
- **Datos de entrada:**
```json
{ "name": "Docker Básico", "targetDate": "2026-09-15" }
```
- **Pasos:** 1) Enviar POST sin `status`, `priority` ni `progress`. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED`; `status = "NO_INICIADO"`, `priority = "MEDIA"`, `progress = 0`.
- **Prioridad:** Alta

### TC-EXI-003
- **Nombre:** Registrar curso con estado `EN_CURSO` y progreso intermedio válido
- **Objetivo:** Verificar consistencia válida entre estado `EN_CURSO` y progreso dentro de rango (1-99).
- **Precondiciones:** No existe un curso con el mismo nombre.
- **Datos de entrada:**
```json
{ "name": "Kubernetes Intro", "status": "EN_CURSO", "priority": "BAJA", "targetDate": "2026-10-10", "progress": 45 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED`; curso creado con `progress = 45`.
- **Prioridad:** Alta

### TC-EXI-004
- **Nombre:** Registrar curso con estado `COMPLETADO` y progreso 100
- **Objetivo:** Verificar consistencia válida entre estado `COMPLETADO` y progreso 100.
- **Precondiciones:** No existe un curso con el mismo nombre.
- **Datos de entrada:**
```json
{ "name": "Git Esencial", "status": "COMPLETADO", "priority": "MEDIA", "targetDate": "2026-08-20", "progress": 100 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED`.
- **Prioridad:** Media

### TC-EXI-005
- **Nombre:** Registrar curso sin descripción
- **Objetivo:** Confirmar que `description` es realmente opcional.
- **Precondiciones:** No existe un curso con el mismo nombre.
- **Datos de entrada:**
```json
{ "name": "Testing con JUnit", "targetDate": "2026-11-05" }
```
- **Pasos:** 1) Enviar POST sin el campo `description`. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED`; `description = null`.
- **Prioridad:** Media

### TC-EXI-006
- **Nombre:** Registrar dos cursos con nombres similares pero no idénticos
- **Objetivo:** Verificar que nombres distintos (aunque parecidos) no disparan la validación de duplicado.
- **Precondiciones:** Existe un curso llamado "Java Básico".
- **Datos de entrada:**
```json
{ "name": "Java Básico II", "targetDate": "2026-09-01" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED`.
- **Prioridad:** Baja

---

## 2. Casos negativos

### TC-NEG-001
- **Nombre:** Registrar curso sin campo `name`
- **Objetivo:** Verificar que el nombre es obligatorio.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "targetDate": "2026-09-01" }
```
- **Pasos:** 1) Enviar POST sin `name`. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje indicando "El nombre del curso es obligatorio". No se crea el curso.
- **Prioridad:** Alta

### TC-NEG-002
- **Nombre:** Registrar curso con `name` en blanco
- **Objetivo:** Verificar que un nombre compuesto solo por espacios es rechazado.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "   ", "targetDate": "2026-09-01" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`, no se crea el curso.
- **Prioridad:** Alta

### TC-NEG-003
- **Nombre:** Registrar curso con nombre duplicado (mismo texto exacto)
- **Objetivo:** Verificar que no se permiten nombres de curso repetidos.
- **Precondiciones:** Ya existe un curso llamado "Angular Fundamentos".
- **Datos de entrada:**
```json
{ "name": "Angular Fundamentos", "targetDate": "2026-09-01" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta. 3) Verificar que no se duplicó el registro en `courses.json`.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje "Ya existe un curso con el nombre: Angular Fundamentos".
- **Prioridad:** Alta

### TC-NEG-004
- **Nombre:** Registrar curso con nombre duplicado (distinto casing)
- **Objetivo:** Verificar que la validación de duplicado es case-insensitive.
- **Precondiciones:** Ya existe un curso llamado "Angular Fundamentos".
- **Datos de entrada:**
```json
{ "name": "ANGULAR fundamentos", "targetDate": "2026-09-01" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST` por nombre duplicado.
- **Prioridad:** Media

### TC-NEG-005
- **Nombre:** Registrar curso sin `targetDate`
- **Objetivo:** Verificar que la fecha objetivo es obligatoria.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso sin fecha" }
```
- **Pasos:** 1) Enviar POST sin `targetDate`. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje "La fecha objetivo es obligatoria".
- **Prioridad:** Alta

### TC-NEG-006
- **Nombre:** Registrar curso con `targetDate` en el pasado
- **Objetivo:** Verificar que se rechaza una fecha objetivo anterior a hoy.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso fecha pasada", "targetDate": "2020-01-01" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje "La fecha objetivo debe ser una fecha futura".
- **Prioridad:** Alta

### TC-NEG-007
- **Nombre:** Registrar curso con `status` inválido
- **Objetivo:** Verificar que solo se aceptan los valores del enum `CourseStatus`.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso status invalido", "status": "PENDIENTE", "targetDate": "2026-09-01" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje "El estado debe ser uno de: NO_INICIADO, EN_CURSO, COMPLETADO".
- **Prioridad:** Alta

### TC-NEG-008
- **Nombre:** Registrar curso con `priority` inválida
- **Objetivo:** Verificar que solo se aceptan los valores del enum `CoursePriority`.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso prioridad invalida", "priority": "URGENTE", "targetDate": "2026-09-01" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje "La prioridad debe ser una de: ALTA, MEDIA, BAJA".
- **Prioridad:** Alta

### TC-NEG-009
- **Nombre:** Registrar curso con `progress` negativo
- **Objetivo:** Verificar el límite inferior del rango de progreso.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso progreso negativo", "targetDate": "2026-09-01", "progress": -5 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje "El progreso debe estar entre 0 y 100".
- **Prioridad:** Alta

### TC-NEG-010
- **Nombre:** Registrar curso con `progress` mayor a 100
- **Objetivo:** Verificar el límite superior del rango de progreso.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso progreso excedido", "targetDate": "2026-09-01", "progress": 150 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje "El progreso debe estar entre 0 y 100".
- **Prioridad:** Alta

### TC-NEG-011
- **Nombre:** Registrar curso con múltiples campos inválidos a la vez
- **Objetivo:** Verificar que se agregan todos los mensajes de validación en una sola respuesta.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "", "targetDate": null, "progress": 500 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje único que concatena los errores de `name`, `targetDate` y `progress` (formato: "Por favor completa los datos obligatorios para continuar: ...").
- **Prioridad:** Media

### TC-NEG-012
- **Nombre:** Registrar curso con body vacío `{}`
- **Objetivo:** Verificar comportamiento cuando no se envía ningún dato.
- **Precondiciones:** Ninguna.
- **Datos de entrada:** `{}`
- **Pasos:** 1) Enviar POST con body `{}`. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST` con errores de `name` y `targetDate` obligatorios.
- **Prioridad:** Alta

### TC-NEG-013
- **Nombre:** Registrar curso sin body / sin `Content-Type: application/json`
- **Objetivo:** Verificar manejo de petición malformada a nivel HTTP.
- **Precondiciones:** Ninguna.
- **Datos de entrada:** Request sin body o con `Content-Type: text/plain`.
- **Pasos:** 1) Enviar POST sin body o con content-type incorrecto. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST` o `415 UNSUPPORTED MEDIA TYPE` (comportamiento por defecto de Spring; no hay manejador explícito, por lo que puede devolver el error estándar de Spring en vez de un JSON con el formato `{"message": ...}` usado en el resto del endpoint — **a confirmar en ejecución**).
- **Prioridad:** Media

---

## 3. Casos límite

### TC-LIM-001
- **Nombre:** Progreso en el límite inferior absoluto (0) con estado `NO_INICIADO`
- **Objetivo:** Verificar el límite exacto permitido para `NO_INICIADO`.
- **Precondiciones:** No existe un curso con el mismo nombre.
- **Datos de entrada:**
```json
{ "name": "Curso limite 0", "status": "NO_INICIADO", "targetDate": "2026-09-01", "progress": 0 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED`.
- **Prioridad:** Media

### TC-LIM-002
- **Nombre:** Progreso en el límite superior absoluto (100) con estado `COMPLETADO`
- **Objetivo:** Verificar el límite exacto permitido para `COMPLETADO`.
- **Precondiciones:** No existe un curso con el mismo nombre.
- **Datos de entrada:**
```json
{ "name": "Curso limite 100", "status": "COMPLETADO", "targetDate": "2026-09-01", "progress": 100 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED`.
- **Prioridad:** Media

### TC-LIM-003
- **Nombre:** Progreso en el límite inferior de `EN_CURSO` (1)
- **Objetivo:** Verificar el límite exacto inferior permitido para `EN_CURSO`.
- **Precondiciones:** No existe un curso con el mismo nombre.
- **Datos de entrada:**
```json
{ "name": "Curso en curso limite 1", "status": "EN_CURSO", "targetDate": "2026-09-01", "progress": 1 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED`.
- **Prioridad:** Media

### TC-LIM-004
- **Nombre:** Progreso en el límite superior de `EN_CURSO` (99)
- **Objetivo:** Verificar el límite exacto superior permitido para `EN_CURSO`.
- **Precondiciones:** No existe un curso con el mismo nombre.
- **Datos de entrada:**
```json
{ "name": "Curso en curso limite 99", "status": "EN_CURSO", "targetDate": "2026-09-01", "progress": 99 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED`.
- **Prioridad:** Media

### TC-LIM-005
- **Nombre:** Inconsistencia estado/progreso — `NO_INICIADO` con progreso distinto de 0
- **Objetivo:** Verificar el rechazo cuando el progreso no corresponde al estado.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso inconsistente 1", "status": "NO_INICIADO", "targetDate": "2026-09-01", "progress": 10 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje "Un curso no iniciado debe tener un progreso del 0%".
- **Prioridad:** Alta

### TC-LIM-006
- **Nombre:** Inconsistencia estado/progreso — `COMPLETADO` con progreso distinto de 100
- **Objetivo:** Verificar el rechazo cuando el progreso no corresponde al estado.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso inconsistente 2", "status": "COMPLETADO", "targetDate": "2026-09-01", "progress": 80 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje "Un curso completado debe tener un progreso del 100%".
- **Prioridad:** Alta

### TC-LIM-007
- **Nombre:** Inconsistencia estado/progreso — `EN_CURSO` con progreso 0
- **Objetivo:** Verificar que `EN_CURSO` no admite progreso 0 (límite justo por debajo del rango permitido).
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso inconsistente 3", "status": "EN_CURSO", "targetDate": "2026-09-01", "progress": 0 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje "Un curso en curso debe tener un progreso entre 1% y 99%".
- **Prioridad:** Alta

### TC-LIM-008
- **Nombre:** Inconsistencia estado/progreso — `EN_CURSO` con progreso 100
- **Objetivo:** Verificar que `EN_CURSO` no admite progreso 100 (límite justo por encima del rango permitido).
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso inconsistente 4", "status": "EN_CURSO", "targetDate": "2026-09-01", "progress": 100 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; mensaje "Un curso en curso debe tener un progreso entre 1% y 99%".
- **Prioridad:** Alta

### TC-LIM-009
- **Nombre:** `targetDate` igual a la fecha actual (hoy)
- **Objetivo:** Verificar si "hoy" se considera fecha futura (`@Future` excluye el día actual).
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso fecha hoy", "targetDate": "2026-08-07" }
```
- **Pasos:** 1) Enviar POST con la fecha del día en curso. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST` (la anotación `@Future` no admite la fecha actual, solo fechas estrictamente posteriores).
- **Prioridad:** Alta

### TC-LIM-010
- **Nombre:** `targetDate` igual a mañana (mínima fecha futura válida)
- **Objetivo:** Verificar el límite inferior exacto de fecha futura aceptada.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso fecha manana", "targetDate": "2026-08-08" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED`.
- **Prioridad:** Media

### TC-LIM-011
- **Nombre:** Nombre de curso con longitud extremadamente larga
- **Objetivo:** Verificar comportamiento ante nombres muy extensos (no hay límite de longitud definido en `name`).
- **Precondiciones:** Ninguna.
- **Datos de entrada:** `name` con 5000 caracteres, `targetDate` válida.
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta y persistencia.
- **Resultado esperado:** Actualmente se espera `201 CREATED` (no hay validación de longitud máxima) — **hallazgo:** debería definirse un límite acorde a RNF-06 / caso límite HU-004.
- **Prioridad:** Media

### TC-LIM-012
- **Nombre:** Descripción con longitud extremadamente larga
- **Objetivo:** Verificar comportamiento ante descripciones muy extensas (sin límite definido).
- **Precondiciones:** Ninguna.
- **Datos de entrada:** `description` con 100,000 caracteres, resto de campos válidos.
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta y tamaño del archivo `courses.json` resultante.
- **Resultado esperado:** Actualmente se espera `201 CREATED` sin truncar — **hallazgo:** riesgo de degradación de rendimiento/almacenamiento al no existir límite de caracteres (contradice el caso límite descrito en HU-004).
- **Prioridad:** Media

### TC-LIM-013
- **Nombre:** `status` y `priority` con espacios extra y mezcla de mayúsculas/minúsculas
- **Objetivo:** Verificar que el parseo normaliza (`trim().toUpperCase()`) antes de convertir al enum.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso normalizacion", "status": "  en_curso ", "priority": "alta", "targetDate": "2026-09-01", "progress": 20 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED`; `status = "EN_CURSO"`, `priority = "ALTA"`.
- **Prioridad:** Baja

---

## 4. Validaciones de formato / tipo de dato

### TC-VAL-001
- **Nombre:** `targetDate` con formato inválido (no ISO-8601)
- **Objetivo:** Verificar el manejo de errores de deserialización de fecha.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso fecha mal formato", "targetDate": "01/09/2026" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`. **A validar:** dado que no existe un `@ExceptionHandler` para `HttpMessageNotReadableException`, es posible que el error devuelto no tenga el mismo formato `{"message": "..."}` que el resto de validaciones, sino la respuesta de error por defecto de Spring — **hallazgo potencial de inconsistencia**.
- **Prioridad:** Alta

### TC-VAL-002
- **Nombre:** `progress` enviado como texto en vez de número
- **Objetivo:** Verificar el manejo de tipos incorrectos en el JSON.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso progreso texto", "targetDate": "2026-09-01", "progress": "cincuenta" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST` por error de deserialización (mismo riesgo de formato de error inconsistente que TC-VAL-001).
- **Prioridad:** Media

### TC-VAL-003
- **Nombre:** JSON malformado (sintaxis inválida)
- **Objetivo:** Verificar el manejo de un payload no parseable.
- **Precondiciones:** Ninguna.
- **Datos de entrada:** `{ "name": "Curso", "targetDate": }` (JSON roto)
- **Pasos:** 1) Enviar POST con el body malformado. 2) Revisar respuesta.
- **Resultado esperado:** `400 BAD REQUEST`.
- **Prioridad:** Media

### TC-VAL-004
- **Nombre:** Campos adicionales no reconocidos en el payload
- **Objetivo:** Verificar que campos extra no rompen la deserialización.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso con campos extra", "targetDate": "2026-09-01", "campoInventado": "valor" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** Depende de la configuración del `ObjectMapper`: si `FAIL_ON_UNKNOWN_PROPERTIES` está deshabilitado, `201 CREATED` ignorando el campo extra; si está habilitado, `400 BAD REQUEST`. **A confirmar en ejecución.**
- **Prioridad:** Baja

### TC-VAL-005
- **Nombre:** `progress` con valor decimal
- **Objetivo:** Verificar el manejo de números no enteros.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso progreso decimal", "targetDate": "2026-09-01", "progress": 45.5 }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta.
- **Resultado esperado:** Jackson trunca/convierte a `Integer` (posible truncamiento silencioso a 45) o falla la deserialización — **a confirmar en ejecución**; de aceptarse, se recomienda documentar el truncamiento como comportamiento esperado.
- **Prioridad:** Baja

---

## 5. Excepciones / manejo de errores

### TC-EXC-001
- **Nombre:** Verificar formato de respuesta ante `IllegalArgumentException`
- **Objetivo:** Confirmar que toda excepción `IllegalArgumentException` (nombre duplicado, estado/prioridad inválidos, inconsistencia estado-progreso) devuelve `400` con body `{"message": "<detalle>"}`.
- **Precondiciones:** Provocar cualquiera de los escenarios de `IllegalArgumentException` (ver TC-NEG-003, 007, 008, TC-LIM-005 a 008).
- **Datos de entrada:** Cualquiera de los payloads asociados.
- **Pasos:** 1) Enviar POST inválido. 2) Inspeccionar estructura exacta del JSON de respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; body `{"message": "..."}` (una sola clave `message`).
- **Prioridad:** Alta

### TC-EXC-002
- **Nombre:** Verificar formato de respuesta ante `MethodArgumentNotValidException`
- **Objetivo:** Confirmar que los errores de Bean Validation se agrupan correctamente en un solo mensaje.
- **Precondiciones:** Enviar payload con múltiples violaciones de anotaciones (`@NotBlank`, `@NotNull`, `@Future`, `@Min`/`@Max`).
- **Datos de entrada:** Ver TC-NEG-011.
- **Pasos:** 1) Enviar POST. 2) Revisar el body de la respuesta.
- **Resultado esperado:** `400 BAD REQUEST`; body `{"message": "Por favor completa los datos obligatorios para continuar: <msg1>, <msg2>, ..."}`. **Nota:** este formato no permite identificar qué campo específico falló mediante programación (frontend), solo lectura humana.
- **Prioridad:** Media

### TC-EXC-003
- **Nombre:** Fallo de escritura en el archivo de persistencia (`courses.json`)
- **Objetivo:** Verificar el comportamiento del sistema si el archivo JSON no puede escribirse (ej. sin permisos, disco lleno, archivo bloqueado por otro proceso).
- **Precondiciones:** Simular archivo `courses.json` de solo lectura o bloqueado externamente.
- **Datos de entrada:** Cualquier payload válido (ver TC-EXI-001).
- **Pasos:** 1) Bloquear/restringir el archivo. 2) Enviar POST válido. 3) Revisar respuesta y logs del servidor.
- **Resultado esperado:** No existe manejo explícito de errores de IO en el controlador; se espera `500 INTERNAL SERVER ERROR` sin un mensaje amigable — **hallazgo:** falta un `@ExceptionHandler` genérico para errores no controlados.
- **Prioridad:** Media

### TC-EXC-004
- **Nombre:** Archivo `courses.json` corrupto o con JSON inválido
- **Objetivo:** Verificar el comportamiento cuando `findAll()` no puede parsear el archivo existente.
- **Precondiciones:** Editar manualmente `courses.json` dejándolo con sintaxis inválida.
- **Datos de entrada:** Cualquier payload válido.
- **Pasos:** 1) Corromper el archivo. 2) Enviar POST válido. 3) Revisar respuesta.
- **Resultado esperado:** Excepción no controlada de Jackson propagada como `500 INTERNAL SERVER ERROR` — **hallazgo:** el sistema no se recupera de forma controlada ante un archivo de datos corrupto.
- **Prioridad:** Media

### TC-EXC-005
- **Nombre:** Condición de carrera al registrar cursos con el mismo nombre en paralelo
- **Objetivo:** Verificar si el sistema previene duplicados bajo concurrencia (registro no es atómico: `findAll` → validar duplicado → `saveAll` son pasos separados).
- **Precondiciones:** No existe un curso con el nombre a probar.
- **Datos de entrada:** Dos requests simultáneos con el mismo `name`:
```json
{ "name": "Curso Concurrente", "targetDate": "2026-09-01" }
```
- **Pasos:** 1) Disparar ambos requests en paralelo (o con una diferencia de milisegundos). 2) Revisar ambas respuestas. 3) Revisar `courses.json` final.
- **Resultado esperado esperado por diseño:** solo uno debería tener éxito (`201`) y el otro debería fallar (`400` por duplicado). **Riesgo identificado:** debido a que el ciclo lectura-validación-escritura no es atómico, es posible que ambos requests pasen la validación de duplicado antes de que cualquiera escriba, resultando en dos cursos con el mismo nombre persistidos. Se recomienda ejecutar este caso como prueba de concurrencia real.
- **Prioridad:** Alta

---

## 6. Casos de seguridad

### TC-SEG-001
- **Nombre:** Inyección de script en el campo `name`/`description` (XSS almacenado)
- **Objetivo:** Verificar si el backend sanitiza o al menos no ejecuta contenido potencialmente peligroso, dado que se persiste tal cual y luego se consume desde el frontend.
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "<script>alert('xss')</script>", "description": "<img src=x onerror=alert(1)>", "targetDate": "2026-09-01" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta. 3) Verificar cómo se almacena en `courses.json`. 4) Verificar renderizado en el frontend al listar el curso.
- **Resultado esperado:** El backend no sanitiza el contenido (no hay lógica de escape); `201 CREATED` con el string tal cual. **Hallazgo de seguridad:** el riesgo de XSS recae completamente en el frontend al renderizar; se recomienda validar que el frontend escape el HTML antes de mostrarlo.
- **Prioridad:** Alta

### TC-SEG-002
- **Nombre:** Ausencia de autenticación/autorización en el endpoint
- **Objetivo:** Verificar que cualquier cliente sin credenciales puede registrar cursos.
- **Precondiciones:** Ninguna.
- **Datos de entrada:** Payload válido cualquiera, sin headers de autenticación.
- **Pasos:** 1) Enviar POST sin token/credenciales. 2) Revisar respuesta.
- **Resultado esperado:** `201 CREATED` (no existe control de acceso). **Hallazgo de seguridad:** dado que es un sistema mono-usuario sin login, cualquier actor con acceso a la red/URL puede crear cursos arbitrariamente; evaluar si aplica para el contexto del MVP o si se requiere protección antes de exponerlo públicamente.
- **Prioridad:** Alta (si el servicio llega a exponerse fuera de localhost)

### TC-SEG-003
- **Nombre:** Verificar restricción de CORS a origen no autorizado
- **Objetivo:** Confirmar que `@CrossOrigin(origins = "http://localhost:5173")` bloquea peticiones desde otros orígenes vía navegador.
- **Precondiciones:** Contar con un cliente web servido desde un origen distinto (ej. `http://localhost:3000`).
- **Datos de entrada:** Payload válido cualquiera, request originado desde `http://localhost:3000`.
- **Pasos:** 1) Ejecutar el POST vía `fetch`/`XHR` desde una página servida en un origen distinto al permitido. 2) Revisar si el navegador bloquea la respuesta por política CORS.
- **Resultado esperado:** El navegador bloquea la respuesta (error CORS) para orígenes distintos a `http://localhost:5173`. **Nota:** esta protección solo aplica a clientes basados en navegador; no protege llamadas directas vía `curl`/Postman/servidor a servidor.
- **Prioridad:** Media

### TC-SEG-004
- **Nombre:** Payload de tamaño excesivo (posible vector de denegación de servicio)
- **Objetivo:** Verificar si existe un límite de tamaño de request configurado a nivel de servidor (Tomcat/Spring), dado que no hay límite de longitud en `name`/`description` a nivel de aplicación.
- **Precondiciones:** Ninguna.
- **Datos de entrada:** `description` con varios MB de texto.
- **Pasos:** 1) Enviar POST con payload de gran tamaño (ej. 10 MB). 2) Revisar respuesta y tiempo de procesamiento.
- **Resultado esperado:** Si no hay límite configurado (`server.tomcat.max-http-form-post-size` u otro), el servidor podría aceptar y procesar el payload completo, generando impacto en memoria/IO — **hallazgo de seguridad/robustez:** se recomienda definir límites de tamaño de payload y de longitud de campos de texto.
- **Prioridad:** Media

### TC-SEG-005
- **Nombre:** Caracteres especiales / Unicode / secuencias de control en `name`
- **Objetivo:** Verificar la robustez del sistema ante entradas con caracteres no estándar (emojis, RTL override, null bytes, etc.).
- **Precondiciones:** Ninguna.
- **Datos de entrada:**
```json
{ "name": "Curso   con caracteres \u202Eraros\u202C 🚀", "targetDate": "2026-09-01" }
```
- **Pasos:** 1) Enviar POST. 2) Revisar respuesta y consistencia del archivo JSON persistido.
- **Resultado esperado:** El sistema debería aceptar o rechazar de forma consistente, sin corromper el archivo `courses.json` ni provocar comportamiento indefinido en el listado posterior.
- **Prioridad:** Baja

---

## Resumen de hallazgos relevantes para la ejecución de pruebas

1. `status` y `priority` son opcionales en el código pese a que HU-004 los describe como obligatorios — validar con negocio cuál es el comportamiento correcto antes de marcar TC-NEG relacionados como bug o como comportamiento esperado.
2. No hay `@ExceptionHandler` para errores de deserialización (`HttpMessageNotReadableException`) ni para errores de IO/persistencia — los casos TC-VAL-001/002 y TC-EXC-003/004 pueden revelar respuestas inconsistentes (formato de error distinto al resto del API).
3. No hay límite de longitud para `name` ni `description` — TC-LIM-011/012 y TC-SEG-004 evalúan este riesgo.
4. El ciclo registrar-curso no es atómico frente a concurrencia — TC-EXC-005 es el caso crítico a ejecutar con herramientas de prueba concurrente (ej. JMeter, k6, o hilos paralelos en un test de integración).
5. No hay control de autenticación/autorización ni sanitización de HTML — TC-SEG-001 y TC-SEG-002 documentan el riesgo, a evaluar según el alcance de exposición real del sistema (RNF-09 indica que debe ser accesible desde navegador sin instalación, lo que sugiere posible exposición pública futura).
