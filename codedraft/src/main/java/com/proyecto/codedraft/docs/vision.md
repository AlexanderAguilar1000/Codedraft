# CodeCraftHub - Documento de Visión

## ¿Qué es CodeCraftHub?

CodeCraftHub es una plataforma web diseñada para ayudar a desarrolladores autodidactas a organizar, priorizar y realizar el seguimiento de los cursos que desean estudiar.

A diferencia de plataformas como Coursera o Udemy, CodeCraftHub no ofrece cursos, sino que funciona como un asistente personal que ayuda al usuario a decidir qué estudiar primero y a mantener un seguimiento constante de su aprendizaje.

---

## Problema que resuelve

Muchos desarrolladores acumulan cursos de distintas plataformas sin una forma clara de organizarlos. Con el tiempo resulta difícil decidir:

- ¿Qué curso debería continuar?
- ¿Cuál tiene mayor prioridad?
- ¿Cuánto he avanzado?
- ¿Qué cursos siguen pendientes?
- ¿Cuál debería terminar primero?

CodeCraftHub centraliza toda esta información y recomienda el siguiente curso que el usuario debería estudiar.

---

## Público objetivo

La plataforma está dirigida a:

- Desarrolladores autodidactas.
- Estudiantes de Ingeniería de Sistemas.
- Programadores junior.
- Personas que realizan cursos en múltiples plataformas.
- Profesionales que desean organizar su aprendizaje continuo.

---

# Propuesta de valor

> CodeCraftHub ayuda a desarrolladores autodidactas a registrar, priorizar y dar seguimiento a sus cursos para decidir fácilmente qué estudiar primero.

---

# Frase brújula del MVP

> Como desarrollador autodidacta, quiero registrar y priorizar mis cursos de aprendizaje para decidir fácilmente qué estudiar primero y hacer seguimiento de mi progreso.

---

# MVP (Producto Mínimo Viable)

El objetivo del MVP es validar la idea principal del producto: ayudar al usuario a organizar y priorizar su aprendizaje de forma sencilla.

## Funcionalidades esenciales de la primera versión

### 1. Perfil del usuario

El usuario completará un formulario inicial donde registrará:

- Rol profesional.
- Carrera.
- Intereses tecnológicos.

Esta información servirá para personalizar las recomendaciones del sistema.

---

### 2. Cursos sugeridos

El sistema mostrará una lista inicial de cursos sugeridos según el perfil y los intereses registrados por el usuario.

---

### 3. Gestión de cursos

El usuario podrá:

- Registrar un curso.
- Consultar la lista de cursos.
- Actualizar la prioridad.
- Actualizar la fecha objetivo.
- Eliminar un curso.

Cada curso almacenará la siguiente información:

- Nombre.
- Descripción.
- Estado (No iniciado, En curso o Completado).
- Prioridad (Alta, Media o Baja).
- Fecha objetivo.
- Progreso (0 a 100%).

---

### 4. Priorización inteligente

El sistema recomendará automáticamente el siguiente curso a estudiar utilizando una regla simple basada en:

1. Prioridad.
2. Estado del curso.
3. Fecha objetivo.

---

### 5. Coach de aprendizaje

CodeCraftHub incluirá un coach virtual que acompañará al usuario durante su proceso de aprendizaje.

El coach utilizará la información del perfil del usuario para generar mensajes motivadores y consejos relacionados con su progreso.

## Implementación mínima del coach

| Idea | Implementación en el MVP |
|------|---------------------------|
| Perfil de aprendizaje | Una API desarrollada en Python generará un perfil de aprendizaje a partir del rol, carrera e intereses del usuario. |
| Consejos motivadores | Se generará un mensaje breve cada vez que el usuario actualice el progreso de un curso. |
| Personaje inspirador | Se utilizará un arquetipo ficticio denominado **Mentor Backend** para acompañar al usuario. |
| Estado numérico | Cada curso tendrá un campo **progress** con valores entre 0 y 100 que representará el avance del curso. |
| Puntos de experiencia | Cada actualización del progreso otorgará puntos de experiencia (por ejemplo, +5 puntos por actualización). |



# Tecnologías propuestas


## Frontend

- HTML
- CSS
- JavaScript
- Interfaz generada con Bolt.new o Bolt.diy

## Backend

- Spring Boot
- Spring Web
- Jackson (para leer y escribir archivos JSON)
- Almacenamiento en archivo JSON

## API de Inteligencia Artificial

- Python
- Flask
- API REST para:
  - Generación del perfil de aprendizaje.
  - Recomendaciones personalizadas.
  - Mensajes del coach virtual.
---

# Objetivo del MVP

Validar que los desarrolladores autodidactas encuentran valor en una herramienta que les permita organizar, priorizar y dar seguimiento a sus cursos antes de incorporar funcionalidades más avanzadas como inteligencia artificial, gamificación completa e integraciones con plataformas de aprendizaje.