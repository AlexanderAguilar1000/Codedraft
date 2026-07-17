# Lógica de recomendación de cursos para CodeCraftHub

## Objetivo

Personalizar las recomendaciones de cursos utilizando la información del
perfil del usuario.

El perfil almacena:

-   Rol profesional
-   Carrera
-   Intereses tecnológicos

## Catálogo de cursos

Cada curso del `catalog.json` puede contener información adicional para
facilitar la recomendación.

``` json
{
  "id": "1",
  "name": "Spring Boot desde Cero",
  "description": "Aprende Spring Boot",
  "roles": ["Backend Developer"],
  "careers": ["Ingeniería de Sistemas"],
  "tags": ["Java", "Spring", "API REST"]
}
```

Otro ejemplo:

``` json
{
  "id": "2",
  "name": "React Fundamentals",
  "description": "Curso de React",
  "roles": ["Frontend Developer"],
  "careers": ["Ingeniería de Sistemas"],
  "tags": ["React", "JavaScript"]
}
```

## Perfil del usuario

``` json
{
  "rol": "Backend Developer",
  "carrera": "Ingeniería de Sistemas",
  "intereses": [
    "Java",
    "Spring",
    "Docker"
  ]
}
```

## Algoritmo de recomendación

En lugar de únicamente filtrar por intereses, cada curso recibe un
puntaje.

Reglas propuestas:

-   Coincidencia de rol: **+3 puntos**
-   Coincidencia de carrera: **+1 punto**
-   Cada interés coincidente: **+2 puntos**

## Ejemplo

### Curso: Spring Boot

-   Rol coincide ✔ (+3)
-   Carrera coincide ✔ (+1)
-   Java ✔ (+2)
-   Spring ✔ (+2)

**Puntaje total: 8**

### Curso: Docker

-   Rol coincide ✔ (+3)
-   Carrera coincide ✔ (+1)
-   Docker ✔ (+2)

**Puntaje total: 6**

### Curso: React

-   Rol no coincide ✘
-   Carrera coincide ✔ (+1)
-   Ningún interés coincide ✘

**Puntaje total: 1**

## Resultado

Los cursos se ordenan de mayor a menor puntaje.

1.  Spring Boot (8)
2.  Docker (6)
3.  React (1)

## ¿Qué ocurre si no hay coincidencias?

Si ningún curso obtiene puntaje, el sistema puede:

-   Mostrar los cursos más populares del catálogo.
-   Mostrar todo el catálogo ordenado alfabéticamente.

De esta forma el usuario siempre recibe recomendaciones.

## Beneficios

Esta estrategia aprovecha toda la información del perfil:

-   **Rol:** identifica el objetivo profesional del usuario.
-   **Carrera:** ayuda a recomendar cursos acordes a su formación.
-   **Intereses:** personalizan las recomendaciones según las
    tecnologías que desea aprender.

Esta solución es sencilla de implementar en el MVP utilizando Spring
Boot y un `catalog.json`. En una versión futura, la lógica puede
reemplazarse por un servicio de IA sin modificar la API ni el frontend.
