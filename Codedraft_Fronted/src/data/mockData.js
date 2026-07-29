// CodeCraftHub — Mock data for the first UI iteration.
// This simulates the JSON payloads returned by the Spring Boot backend
// (profile.json, courses.json, catalog.json). When the API layer is
// connected, these mocks are replaced by real fetch calls.

export const mockProfile = {
  rol: 'Backend Developer',
  carrera: 'Ingeniería de Sistemas',
  intereses: ['Java', 'Spring Boot', 'APIs REST', 'Microservicios'],
  experiencePoints: 35,
};

export const mockCourses = [
  {
    id: '3f2f1a2c-9b7a-4a1d-9d3a-6b0c8b5e1234',
    name: 'Spring Boot desde cero',
    description: 'Curso de fundamentos de Spring Boot y Spring Web.',
    status: 'EN_CURSO',
    priority: 'ALTA',
    targetDate: '2026-09-30',
    progress: 40,
  },
  {
    id: '8a1b2c3d-4e5f-6789-abcd-ef0123456789',
    name: 'JavaScript Moderno',
    description: 'ES6+, async/await y manipulación del DOM.',
    status: 'NO_INICIADO',
    priority: 'MEDIA',
    targetDate: '2026-11-15',
    progress: 0,
  },
  {
    id: 'c1d2e3f4-5a6b-7c8d-9e0f-1a2b3c4d5e6f',
    name: 'Docker para desarrolladores',
    description: 'Contenedores, imágenes y despliegue de aplicaciones.',
    status: 'EN_CURSO',
    priority: 'ALTA',
    targetDate: '2026-08-20',
    progress: 65,
  },
  {
    id: 'd2e3f4a5-6b7c-8d9e-0f1a-2b3c4d5e6f7a',
    name: 'Bases de datos PostgreSQL',
    description: 'Modelado relacional, consultas avanzadas y optimización.',
    status: 'COMPLETADO',
    priority: 'MEDIA',
    targetDate: '2026-06-10',
    progress: 100,
  },
  {
    id: 'e3f4a5b6-7c8d-9e0f-1a2b-3c4d5e6f7a8b',
    name: 'Microservicios con Spring Cloud',
    description: 'Arquitectura de microservicios, gateway y service discovery.',
    status: 'NO_INICIADO',
    priority: 'BAJA',
    targetDate: '2027-01-20',
    progress: 0,
  },
  {
    id: 'f4a5b6c7-8d9e-0f1a-2b3c-4d5e6f7a8b9c',
    name: 'Git y GitHub Workflow',
    description: 'Control de versiones, branching y colaboración en equipo.',
    status: 'COMPLETADO',
    priority: 'BAJA',
    targetDate: '2026-05-01',
    progress: 100,
  },
  {
    id: 'a5b6c7d8-9e0f-1a2b-3c4d-5e6f7a8b9c0d',
    name: 'REST APIs con Node.js',
    description: 'Diseño y consumo de APIs REST con Express.',
    status: 'EN_CURSO',
    priority: 'MEDIA',
    targetDate: '2026-10-05',
    progress: 25,
  },
  {
    id: 'b6c7d8e9-0f1a-2b3c-4d5e-6f7a8b9c0d1e',
    name: 'Testing con JUnit 5',
    description: 'Pruebas unitarias y de integración para aplicaciones Java.',
    status: 'NO_INICIADO',
    priority: 'MEDIA',
    targetDate: '2026-12-15',
    progress: 0,
  },
  {
    id: 'c7d8e9f0-1a2b-3c4d-5e6f-7a8b9c0d1e2f',
    name: 'Kubernetes Fundamentals',
    description: 'Orquestación de contenedores para entornos de producción.',
    status: 'NO_INICIADO',
    priority: 'BAJA',
    targetDate: '2027-02-28',
    progress: 0,
  },
  {
    id: 'd8e9f0a1-2b3c-4d5e-6f7a-8b9c0d1e2f3a',
    name: 'Patrones de Diseño en Java',
    description: 'Singleton, Factory, Observer y los patrones más usados.',
    status: 'EN_CURSO',
    priority: 'ALTA',
    targetDate: '2026-09-12',
    progress: 55,
  },
];

export const mockCatalog = [
  {
    id: 'cat-001',
    name: 'Spring Boot Avanzado',
    description: 'Microservicios y APIs REST con Spring Boot.',
    roles: ['Backend Developer'],
    careers: ['Ingeniería de Sistemas'],
    tags: ['Java', 'Spring Boot', 'Microservicios'],
  },
  {
    id: 'cat-002',
    name: 'Introducción a Docker',
    description: 'Contenedores para despliegue de aplicaciones.',
    roles: ['Backend Developer', 'DevOps'],
    careers: ['Ingeniería de Sistemas'],
    tags: ['DevOps', 'Docker'],
  },
  {
    id: 'cat-003',
    name: 'AWS Certified Developer',
    description: 'Servicios cloud y despliegue de aplicaciones en AWS.',
    roles: ['Backend Developer', 'DevOps', 'Cloud Engineer'],
    careers: ['Ingeniería de Sistemas'],
    tags: ['Cloud', 'AWS', 'DevOps'],
  },
  {
    id: 'cat-004',
    name: 'Arquitectura Hexagonal',
    description: 'Diseño de aplicaciones mantenibles y testeables.',
    roles: ['Backend Developer', 'Software Architect'],
    careers: ['Ingeniería de Sistemas'],
    tags: ['Java', 'Arquitectura', 'Microservicios'],
  },
  {
    id: 'cat-005',
    name: 'Frontend con React',
    description: 'Componentes, hooks y estado en aplicaciones modernas.',
    roles: ['Frontend Developer', 'Fullstack Developer'],
    careers: ['Ingeniería de Sistemas'],
    tags: ['JavaScript', 'React', 'Frontend'],
  },
  {
    id: 'cat-006',
    name: 'Seguridad en APIs REST',
    description: 'OAuth2, JWT y buenas prácticas de seguridad.',
    roles: ['Backend Developer', 'Security Engineer'],
    careers: ['Ingeniería de Sistemas'],
    tags: ['APIs REST', 'Seguridad', 'Java'],
  },
  {
    id: 'cat-007',
    name: 'CI/CD con GitHub Actions',
    description: 'Pipelines automatizados para integración y despliegue.',
    roles: ['DevOps', 'Backend Developer'],
    careers: ['Ingeniería de Sistemas'],
    tags: ['DevOps', 'CI/CD', 'GitHub'],
  },
  {
    id: 'cat-008',
    name: 'Redis para caching',
    description: 'Caching, sesiones y colas con Redis.',
    roles: ['Backend Developer'],
    careers: ['Ingeniería de Sistemas'],
    tags: ['Java', 'APIs REST', 'Performance'],
  },
];

// Copy of the interests catalogue offered in the profile form.
export const interestOptions = [
  'Java',
  'Spring Boot',
  'APIs REST',
  'Microservicios',
  'Docker',
  'Kubernetes',
  'Cloud',
  'AWS',
  'DevOps',
  'CI/CD',
  'JavaScript',
  'React',
  'Node.js',
  'PostgreSQL',
  'Testing',
  'Seguridad',
  'Arquitectura',
  'GitHub',
];

// Common professional roles for the profile dropdown.
export const roleOptions = [
  'Backend Developer',
  'Frontend Developer',
  'Fullstack Developer',
  'DevOps',
  'Cloud Engineer',
  'Software Architect',
  'Security Engineer',
  'Data Engineer',
  'Mobile Developer',
];

// Common careers for the profile dropdown.
export const careerOptions = [
  'Ingeniería de Sistemas',
  'Ingeniería de Software',
  'Ingeniería Informática',
  'Ciencias de la Computación',
  'Tecnologías de la Información',
  'Otra',
];
