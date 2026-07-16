# CodeCraftHub - Historias de Usuario

Basado en el [Documento de Requisitos](./requeriments.md).

---

## HU-001 Completar perfil de usuario

**Como un** desarrollador autodidacta
**Quiero** completar un formulario inicial con mi rol profesional, carrera e intereses tecnológicos
**Para que** el sistema pueda personalizar mis recomendaciones de cursos y mensajes del coach

- **Prioridad:** Alta
- **Requisito relacionado:** RF-01

**Criterios de aceptación**
- Dado que soy un usuario nuevo, cuando accedo por primera vez al sistema, entonces se me presenta un formulario de perfil con los campos rol, carrera e intereses tecnológicos.
- Dado que completo todos los campos obligatorios, cuando envío el formulario, entonces el perfil queda guardado y asociado a mi cuenta.
- Dado que intento enviar el formulario con campos obligatorios vacíos, cuando presiono guardar, entonces el sistema muestra un mensaje de validación y no persiste el perfil.

**Casos límite**
- El usuario selecciona múltiples intereses tecnológicos: el sistema debe aceptar una lista, no solo un valor único.
- El usuario intenta acceder a otras funcionalidades (cursos, coach) sin haber completado el perfil.
- El usuario abandona el formulario a mitad de proceso y regresa después: debe conservarse o reiniciarse el progreso de forma predecible.

**Dependencias**
- Ninguna (historia base).

---

## HU-002 Recibir recomendaciones personalizadas según el perfil

**Como un** desarrollador autodidacta
**Quiero** que el sistema use los datos de mi perfil (rol, carrera, intereses)
**Para que** las recomendaciones de cursos que reciba sean relevantes para mí

- **Prioridad:** Alta
- **Requisito relacionado:** RF-02

**Criterios de aceptación**
- Dado que mi perfil está completo, cuando el sistema genera recomendaciones, entonces estas se basan en rol, carrera e intereses registrados.
- Dado que actualizo mi perfil, cuando vuelvo a solicitar recomendaciones, entonces estas reflejan los nuevos datos.

**Casos límite**
- El usuario tiene un perfil con intereses muy genéricos o vacíos tras una actualización parcial: el sistema debe manejar la ausencia de datos sin fallar.
- El servicio de recomendaciones (API Python) no responde: el sistema debe degradar de forma controlada (mensaje de error o lista vacía) sin bloquear la aplicación.

**Dependencias**
- Depende de HU-001 (perfil de usuario debe existir).

---

## HU-003 Ver cursos sugeridos iniciales

**Como un** desarrollador autodidacta
**Quiero** ver una lista inicial de cursos sugeridos según mi perfil
**Para que** pueda descubrir por dónde empezar mi aprendizaje sin buscar por mi cuenta

- **Prioridad:** Alta
- **Requisito relacionado:** RF-03

**Criterios de aceptación**
- Dado que completé mi perfil, cuando ingreso al sistema por primera vez, entonces veo una lista de cursos sugeridos relacionada con mis intereses.
- Dado que la lista de sugerencias se muestra, cuando no hay coincidencias exactas con mis intereses, entonces el sistema muestra sugerencias generales en lugar de una lista vacía.

**Casos límite**
- El usuario tiene intereses muy específicos o poco comunes y no existen cursos sugeridos asociados.
- El usuario recarga la página varias veces: la lista sugerida debe mantenerse consistente (no generar resultados aleatorios cada vez, salvo que sea el comportamiento esperado).

**Dependencias**
- Depende de HU-001 (perfil de usuario) y HU-002 (personalización mediante perfil).

---

## HU-004 Registrar un nuevo curso

**Como un** desarrollador autodidacta
**Quiero** registrar un curso con nombre, descripción, estado, prioridad, fecha objetivo y progreso
**Para que** pueda llevar control centralizado de todo lo que quiero estudiar

- **Prioridad:** Alta
- **Requisito relacionado:** RF-04, RNF-06

**Criterios de aceptación**
- Dado que estoy en la sección de cursos, cuando completo el formulario con datos válidos, entonces el curso se guarda y aparece en mi lista.
- Dado que dejo un campo obligatorio vacío (nombre, estado o prioridad), cuando intento guardar, entonces el sistema muestra un error de validación y no crea el curso.
- Dado que ingreso un progreso fuera del rango 0-100, cuando intento guardar, entonces el sistema rechaza el valor y muestra un mensaje de validación.

**Casos límite**
- Se ingresa una fecha objetivo en el pasado: el sistema debe decidir si la permite con advertencia o la rechaza.
- Se ingresa un nombre de curso duplicado: definir si se permite o se advierte al usuario.
- Se ingresa una descripción extremadamente larga: debe existir un límite de caracteres validado.

**Dependencias**
- Depende de HU-001 (el curso se asocia a un usuario con perfil creado).

---

## HU-005 Consultar la lista de cursos registrados

**Como un** desarrollador autodidacta
**Quiero** consultar la lista de todos mis cursos registrados
**Para que** pueda tener una visión general de mi plan de estudio

- **Prioridad:** Alta
- **Requisito relacionado:** RF-05

**Criterios de aceptación**
- Dado que tengo cursos registrados, cuando accedo a la sección de cursos, entonces veo el listado completo con nombre, estado, prioridad, fecha objetivo y progreso.
- Dado que no tengo cursos registrados, cuando accedo a la sección de cursos, entonces el sistema muestra un mensaje indicando que la lista está vacía.

**Casos límite**
- El usuario tiene un número muy alto de cursos registrados: la lista debe soportar paginación o scroll sin degradar el rendimiento.
- Fallo de conexión con el backend al cargar la lista: debe mostrarse un mensaje de error claro en lugar de una pantalla en blanco.

**Dependencias**
- Depende de HU-004 (deben existir cursos registrados para poder consultarlos).

---

## HU-006 Actualizar la prioridad de un curso

**Como un** desarrollador autodidacta
**Quiero** actualizar la prioridad (Alta, Media, Baja) de un curso
**Para que** el sistema me recomiende correctamente qué estudiar primero

- **Prioridad:** Media
- **Requisito relacionado:** RF-06

**Criterios de aceptación**
- Dado que selecciono un curso existente, cuando cambio su prioridad a un valor válido (Alta, Media, Baja), entonces el cambio se guarda y se refleja en la lista de cursos.
- Dado que intento asignar un valor de prioridad no permitido, cuando intento guardar, entonces el sistema rechaza el cambio.

**Casos límite**
- Se actualiza la prioridad de un curso ya marcado como "Completado": definir si el sistema lo permite o lo bloquea.
- Dos actualizaciones simultáneas sobre el mismo curso (por ejemplo, desde dos pestañas): debe prevalecer un comportamiento consistente (última escritura gana, con control de concurrencia básico).

**Dependencias**
- Depende de HU-004 (el curso debe existir previamente).
- Impacta a HU-011 (priorización inteligente usa este campo).

---

## HU-007 Actualizar la fecha objetivo de un curso

**Como un** desarrollador autodidacta
**Quiero** actualizar la fecha objetivo de un curso
**Para que** pueda ajustar mis plazos de estudio según mi disponibilidad real

- **Prioridad:** Media
- **Requisito relacionado:** RF-07

**Criterios de aceptación**
- Dado que selecciono un curso existente, cuando ingreso una nueva fecha objetivo válida, entonces el cambio se guarda y se refleja en la lista de cursos.
- Dado que ingreso una fecha con formato inválido, cuando intento guardar, entonces el sistema muestra un error de validación.

**Casos límite**
- Se ingresa una fecha objetivo anterior a la fecha actual.
- Se elimina la fecha objetivo (campo vacío) en un curso que antes la tenía: debe definirse si el campo es obligatorio o puede quedar nulo.

**Dependencias**
- Depende de HU-004 (el curso debe existir previamente).
- Impacta a HU-011 (priorización inteligente usa este campo).

---

## HU-008 Actualizar el progreso de un curso

**Como un** desarrollador autodidacta
**Quiero** actualizar el progreso de un curso con un valor entre 0 y 100
**Para que** pueda visualizar mi avance real y recibir el reconocimiento del coach

- **Prioridad:** Alta
- **Requisito relacionado:** RF-08, RNF-06

**Criterios de aceptación**
- Dado que selecciono un curso, cuando actualizo el progreso con un valor entre 0 y 100, entonces el cambio se guarda correctamente.
- Dado que ingreso un valor de progreso menor a 0 o mayor a 100, cuando intento guardar, entonces el sistema rechaza el valor.
- Dado que actualizo el progreso, cuando la actualización es exitosa, entonces se dispara la generación de un mensaje motivador del coach y la asignación de puntos de experiencia.

**Casos límite**
- El progreso se actualiza a 100: el sistema debe evaluar si corresponde cambiar automáticamente el estado del curso a "Completado".
- Se intenta bajar el progreso de un valor mayor a uno menor (retroceso): definir si está permitido.
- Se actualiza el progreso repetidamente en un corto periodo de tiempo: evaluar si se deben limitar los puntos de experiencia otorgados para evitar abuso.

**Dependencias**
- Depende de HU-004 (el curso debe existir previamente).
- Dispara HU-013 (mensajes motivadores del coach) y HU-015 (puntos de experiencia).

---

## HU-009 Eliminar un curso

**Como un** desarrollador autodidacta
**Quiero** eliminar un curso registrado
**Para que** pueda mantener mi lista de aprendizaje limpia y relevante

- **Prioridad:** Media
- **Requisito relacionado:** RF-09

**Criterios de aceptación**
- Dado que selecciono un curso existente, cuando confirmo la eliminación, entonces el curso desaparece de mi lista de forma permanente.
- Dado que solicito eliminar un curso, cuando el sistema muestra la confirmación, entonces puedo cancelar la acción sin que se elimine el curso.

**Casos límite**
- Se intenta eliminar un curso que ya fue eliminado previamente (por ejemplo, en otra pestaña): el sistema debe manejar el error sin romper la interfaz.
- Se elimina un curso con progreso avanzado o puntos de experiencia asociados: definir si los puntos de experiencia ya otorgados se conservan o se revierten.

**Dependencias**
- Depende de HU-004 (el curso debe existir previamente).

---

## HU-010 Clasificar el estado del curso

**Como un** desarrollador autodidacta
**Quiero** clasificar cada curso en un estado (No iniciado, En curso o Completado)
**Para que** pueda identificar rápidamente en qué etapa se encuentra cada curso

- **Prioridad:** Alta
- **Requisito relacionado:** RF-10

**Criterios de aceptación**
- Dado que registro o edito un curso, cuando selecciono un estado, entonces solo puedo elegir entre "No iniciado", "En curso" o "Completado".
- Dado que un curso alcanza el 100% de progreso, cuando se guarda esa actualización, entonces el sistema sugiere o aplica automáticamente el estado "Completado".

**Casos límite**
- Un curso se marca como "Completado" manualmente con progreso menor a 100: definir si el sistema ajusta el progreso automáticamente o permite la inconsistencia.
- Un curso se marca como "No iniciado" teniendo progreso mayor a 0: debe definirse una regla de consistencia entre estado y progreso.

**Dependencias**
- Depende de HU-004 (el curso debe existir previamente).
- Relacionada con HU-008 (actualización de progreso).

---

## HU-011 Recibir recomendación del siguiente curso a estudiar

**Como un** desarrollador autodidacta
**Quiero** que el sistema me recomiende automáticamente el siguiente curso a estudiar
**Para que** no tenga que decidir manualmente qué priorizar cada vez

- **Prioridad:** Alta
- **Requisito relacionado:** RF-11

**Criterios de aceptación**
- Dado que tengo varios cursos registrados, cuando solicito una recomendación, entonces el sistema aplica la regla basada en prioridad, estado y fecha objetivo para sugerir un curso.
- Dado que existen varios cursos con la misma prioridad, cuando el sistema calcula la recomendación, entonces se usa la fecha objetivo más próxima como criterio de desempate.
- Dado que todos mis cursos están "Completados", cuando solicito una recomendación, entonces el sistema indica que no hay cursos pendientes.

**Casos límite**
- Existen cursos con la misma prioridad y la misma fecha objetivo: debe definirse un criterio de desempate adicional (por ejemplo, orden de creación).
- No existen cursos registrados: el sistema debe mostrar un mensaje apropiado en lugar de un error.
- Un curso de alta prioridad ya está "Completado": no debe ser recomendado nuevamente.

**Dependencias**
- Depende de HU-004, HU-006, HU-007 y HU-010 (requiere cursos con prioridad, fecha objetivo y estado definidos).

---

## HU-012 Generar perfil de aprendizaje para el coach

**Como un** desarrollador autodidacta
**Quiero** que el sistema genere un perfil de aprendizaje a partir de mi rol, carrera e intereses
**Para que** el coach virtual pueda acompañarme de forma personalizada

- **Prioridad:** Media
- **Requisito relacionado:** RF-12, RNF-04, RNF-08

**Criterios de aceptación**
- Dado que mi perfil de usuario está completo, cuando el sistema invoca la API de IA, entonces se genera un perfil de aprendizaje asociado a mi cuenta.
- Dado que actualizo mis datos de perfil, cuando se vuelve a generar el perfil de aprendizaje, entonces este refleja los cambios más recientes.

**Casos límite**
- La API de recomendaciones (Python/Flask) no está disponible: el sistema principal debe seguir funcionando sin bloquear otras funcionalidades.
- El perfil de usuario tiene datos incompletos al momento de generar el perfil de aprendizaje: debe manejarse sin generar errores no controlados.

**Dependencias**
- Depende de HU-001 (perfil de usuario).

---

## HU-013 Recibir mensajes motivadores del coach

**Como un** desarrollador autodidacta
**Quiero** recibir un mensaje motivador breve cada vez que actualizo el progreso de un curso
**Para que** me sienta acompañado y motivado a continuar aprendiendo

- **Prioridad:** Media
- **Requisito relacionado:** RF-13

**Criterios de aceptación**
- Dado que actualizo el progreso de un curso, cuando la actualización se guarda correctamente, entonces se muestra un mensaje motivador generado por el coach.
- Dado que el servicio de IA no responde, cuando actualizo el progreso, entonces el progreso se guarda igualmente aunque no se muestre el mensaje del coach.

**Casos límite**
- Actualizaciones de progreso muy frecuentes en poco tiempo: evaluar si se debe limitar la frecuencia de mensajes para no saturar al usuario.
- El servicio de IA responde con demora: la actualización de progreso no debe quedar bloqueada esperando el mensaje.

**Dependencias**
- Depende de HU-008 (actualización de progreso) y HU-012 (perfil de aprendizaje generado).

---

## HU-014 Interactuar con el personaje del coach

**Como un** desarrollador autodidacta
**Quiero** que los mensajes del coach se presenten a través de un personaje llamado "Mentor Backend"
**Para que** la experiencia de aprendizaje se sienta más cercana y motivadora

- **Prioridad:** Baja
- **Requisito relacionado:** RF-14

**Criterios de aceptación**
- Dado que se genera un mensaje del coach, cuando se muestra en la interfaz, entonces se presenta asociado al personaje "Mentor Backend" (nombre y/o avatar).
- Dado que se muestran distintos mensajes en distintos momentos, cuando el usuario los recibe, entonces todos mantienen la misma identidad visual del personaje.

**Casos límite**
- Falta el recurso gráfico (avatar) del personaje: el mensaje debe mostrarse igualmente en formato texto.

**Dependencias**
- Depende de HU-013 (mensajes motivadores del coach).

---

## HU-015 Ganar puntos de experiencia al actualizar el progreso

**Como un** desarrollador autodidacta
**Quiero** ganar puntos de experiencia cada vez que actualizo el progreso de un curso
**Para que** perciba una sensación de logro y avance en mi proceso de aprendizaje

- **Prioridad:** Media
- **Requisito relacionado:** RF-15

**Criterios de aceptación**
- Dado que actualizo el progreso de un curso exitosamente, cuando la actualización se guarda, entonces se otorgan puntos de experiencia (por ejemplo, +5 puntos).
- Dado que consulto mi perfil, cuando reviso mis puntos, entonces veo el total acumulado de puntos de experiencia.

**Casos límite**
- Se actualiza el progreso al mismo valor que ya tenía (sin cambio real): definir si se otorgan puntos nuevamente o no.
- Se elimina un curso (HU-009) que ya había otorgado puntos: definir si los puntos se conservan o se descuentan.
- Actualizaciones masivas o repetidas en poco tiempo: evaluar reglas anti-abuso para el otorgamiento de puntos.

**Dependencias**
- Depende de HU-008 (actualización de progreso).
