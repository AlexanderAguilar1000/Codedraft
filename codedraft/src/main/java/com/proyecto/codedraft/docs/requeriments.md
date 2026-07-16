# CodeCraftHub - Documento de Requisitos

Basado en el [Documento de Visión](./vision.md).

---

## Requisitos Funcionales

### RF-01 Perfil de usuario
El sistema debe permitir al usuario completar un formulario inicial de perfil registrando: rol profesional, carrera e intereses tecnológicos.

### RF-02 Personalización mediante perfil
El sistema debe utilizar los datos del perfil (rol, carrera, intereses) para personalizar las recomendaciones de cursos.

### RF-03 Cursos sugeridos
El sistema debe mostrar una lista inicial de cursos sugeridos en función del perfil e intereses registrados por el usuario.

### RF-04 Registrar curso
El sistema debe permitir al usuario registrar un nuevo curso con los siguientes datos: nombre, descripción, estado, prioridad, fecha objetivo y progreso.

### RF-05 Consultar cursos
El sistema debe permitir al usuario consultar la lista de cursos registrados.

### RF-06 Actualizar prioridad de curso
El sistema debe permitir al usuario actualizar la prioridad (Alta, Media, Baja) de un curso registrado.

### RF-07 Actualizar fecha objetivo de curso
El sistema debe permitir al usuario actualizar la fecha objetivo de un curso registrado.

### RF-08 Actualizar progreso de curso
El sistema debe permitir al usuario actualizar el progreso de un curso, con valores entre 0 y 100.

### RF-09 Eliminar curso
El sistema debe permitir al usuario eliminar un curso registrado.

### RF-10 Estado del curso
El sistema debe permitir clasificar cada curso en uno de los siguientes estados: No iniciado, En curso o Completado.

### RF-11 Priorización inteligente
El sistema debe recomendar automáticamente el siguiente curso a estudiar, aplicando una regla basada en: prioridad, estado del curso y fecha objetivo.

### RF-12 Generación de perfil de aprendizaje (Coach)
El sistema debe generar un perfil de aprendizaje a partir del rol, carrera e intereses del usuario mediante una API de recomendaciones.

### RF-13 Mensajes motivadores del coach
El sistema debe generar un mensaje breve y motivador cada vez que el usuario actualice el progreso de un curso.

### RF-14 Personaje del coach
El sistema debe presentar los mensajes del coach a través de un arquetipo ficticio denominado "Mentor Backend".

### RF-15 Puntos de experiencia
El sistema debe otorgar puntos de experiencia al usuario cada vez que actualice el progreso de un curso (por ejemplo, +5 puntos por actualización).

---

## Requisitos No Funcionales

### RNF-01 Arquitectura del backend
El backend debe implementarse con Spring Boot y Spring Web.

### RNF-02 Persistencia de datos
El sistema no debe utilizar un motor de base de datos. La información (perfil, cursos y catálogo) debe almacenarse y leerse desde archivos JSON mediante Jackson.

### RNF-03 Frontend
La interfaz de usuario debe implementarse con HTML, CSS y JavaScript, generada con Bolt.new o Bolt.diy.

### RNF-04 Servicio de inteligencia artificial
Las funcionalidades de recomendación y coach deben implementarse como una API REST independiente desarrollada en Python con Flask.

### RNF-05 Usabilidad
La plataforma debe ofrecer una interfaz simple e intuitiva que permita al usuario gestionar sus cursos sin necesidad de capacitación previa.

### RNF-06 Validación de datos
El sistema debe validar los datos ingresados por el usuario (por ejemplo, progreso entre 0 y 100, campos obligatorios del curso) antes de persistirlos.

### RNF-07 Escalabilidad del MVP
La arquitectura debe permitir incorporar en el futuro funcionalidades adicionales (gamificación completa, integraciones con plataformas de aprendizaje) sin rediseñar el núcleo del sistema.

### RNF-08 Separación de responsabilidades
El servicio de inteligencia artificial (coach y recomendaciones) debe mantenerse desacoplado del backend principal, comunicándose mediante una API REST.

### RNF-09 Disponibilidad
El sistema debe estar disponible como aplicación web accesible desde un navegador, sin requerir instalación por parte del usuario.

### RNF-10 Mantenibilidad
El código debe organizarse siguiendo las convenciones estándar de Spring Boot y buenas prácticas de JavaScript para facilitar su mantenimiento y extensión futura.
