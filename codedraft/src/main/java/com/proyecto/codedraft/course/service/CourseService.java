package com.proyecto.codedraft.course.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.proyecto.codedraft.course.dto.CourseRequest;
import com.proyecto.codedraft.course.model.Course;
import com.proyecto.codedraft.course.model.CoursePriority;
import com.proyecto.codedraft.course.model.CourseStatus;
import com.proyecto.codedraft.course.repositorio.CourseRepository;
import com.proyecto.codedraft.profile.service.ProfileService;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ProfileService profileService;
    private final int experiencePointsPerProgressUpdate;

    public CourseService(CourseRepository courseRepository,
                          ProfileService profileService,
                          @Value("${app.experience.points-per-progress-update:5}") int experiencePointsPerProgressUpdate) {
        this.courseRepository = courseRepository;
        this.profileService = profileService;
        this.experiencePointsPerProgressUpdate = experiencePointsPerProgressUpdate;
    }

    public Course registerCourse(CourseRequest request) {

        //recibe el curso  y valida que tenga el nombre del curso 
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("El nombre del curso es obligatorio");
        }

        // Validar que no exista un curso con el mismo nombre
        List<Course> existingCourses = courseRepository.findAll();
        boolean nameExists = existingCourses.stream()
                .anyMatch(course -> course.getName().equalsIgnoreCase(request.getName()));
        if (nameExists) {
            throw new IllegalArgumentException("Ya existe un curso con el nombre: " + request.getName());
        }

        CourseStatus status = StringUtils.hasText(request.getStatus())
                ? parseStatus(request.getStatus())//obtener el estado
                : CourseStatus.NO_INICIADO;
        CoursePriority priority = StringUtils.hasText(request.getPriority())
                ? parsePriority(request.getPriority()) //obtener la prioridad
                : CoursePriority.MEDIA;
        LocalDate targetDate = request.getTargetDate(); // Ya viene validado como LocalDate desde el DTO
        int progress = request.getProgress() != null ? request.getProgress() : 0;

        // Validar consistencia entre status y progress
        validateStatusProgressConsistency(status, progress);

        Course course = new Course(UUID.randomUUID().toString(), request.getName(), request.getDescription(),
                status, priority, targetDate, progress);

        existingCourses.add(course);//lo agrega a la lista de cursos
        courseRepository.saveAll(existingCourses);// lo guarda en la base de datos
        return course;//devuelve el curso que creo
    }

    public List<Course> listCourses() {
        return courseRepository.findAll();
    }

    public Course updateStatus(String id, String rawStatus) {
        CourseStatus status = parseStatus(rawStatus);
        List<Course> courses = courseRepository.findAll();//lista de cursos 
        Course course = findByIdOrThrow(courses, id);//encontramos el curso por id 
        course.setStatus(status);//cambiamos el estado del curso 
        courseRepository.saveAll(courses);
        return course;
    }

    public Course updatePriority(String id, String rawPriority) {
        CoursePriority priority = parsePriority(rawPriority);
        List<Course> courses = courseRepository.findAll();
        Course course = findByIdOrThrow(courses, id);
        course.setPriority(priority);
        courseRepository.saveAll(courses);
        return course;
    }

    public Course updateProgress(String id, int progress) {
        List<Course> courses = courseRepository.findAll();
        Course course = findByIdOrThrow(courses, id);

        // Validar consistencia entre status y progress
        validateStatusProgressConsistency(course.getStatus(), progress);

        course.setProgress(progress);
        courseRepository.saveAll(courses);

        // Cada actualización exitosa de progreso otorga puntos de experiencia al perfil
        profileService.addExperiencePoints(experiencePointsPerProgressUpdate);

        return course;
    }

    //actualiza la fecha objetivo del curso
    public Course updateTargetDate(String id, LocalDate targetDate) {
        if (targetDate == null) {
            throw new IllegalArgumentException("La fecha objetivo es obligatoria");
        }
        List<Course> courses = courseRepository.findAll();
        Course course = findByIdOrThrow(courses, id);
        course.setTargetDate(targetDate);
        courseRepository.saveAll(courses);
        return course;
    }

    //elimina un curso por id
    public void deleteCourse(String id) {
        List<Course> courses = courseRepository.findAll();
        Course course = findByIdOrThrow(courses, id);
        courses.remove(course);
        courseRepository.saveAll(courses);
    }

    //obtiene el siguiente curso recomendado: cursos pendientes ordenados por
    //prioridad (alta primero), estado (en curso antes que no iniciado) y fecha objetivo (mas cercana primero)
    public Optional<Course> getRecommendation() {
        return courseRepository.findAll().stream()
                .filter(course -> course.getStatus() != CourseStatus.COMPLETADO)
                .sorted(Comparator
                        .comparingInt((Course course) -> priorityWeight(course.getPriority()))
                        .thenComparingInt(course -> statusWeight(course.getStatus()))
                        .thenComparing(Course::getTargetDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .findFirst();
    }

    private int priorityWeight(CoursePriority priority) {
        return switch (priority) {
            case ALTA -> 0;
            case MEDIA -> 1;
            case BAJA -> 2;
        };
    }

    private int statusWeight(CourseStatus status) {
        return switch (status) {
            case EN_CURSO -> 0;
            case NO_INICIADO -> 1;
            case COMPLETADO -> 2;
        };
    }

   //si se encontro el curso perfecto si no lanza una excepcion
    private Course findByIdOrThrow(List<Course> courses, String id) {
        return courses.stream()
                .filter(course -> course.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new CourseNotFoundException("No se encontro un curso con id " + id));
    }

    private CourseStatus parseStatus(String rawStatus) {
        try {
            return CourseStatus.valueOf(rawStatus.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "El estado debe ser uno de: NO_INICIADO, EN_CURSO, COMPLETADO");
        }
    }

    private CoursePriority parsePriority(String rawPriority) {
        try {
            return CoursePriority.valueOf(rawPriority.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("La prioridad debe ser una de: ALTA, MEDIA, BAJA");
        }
    }

    private LocalDate parseTargetDate(String rawTargetDate) {
        try {
            return LocalDate.parse(rawTargetDate.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("La fecha objetivo debe tener el formato yyyy-MM-dd");
        }
    }

    /**
     * Valida la consistencia entre el estado del curso y su progreso.
     * - Si el curso está COMPLETADO, el progreso debe ser 100
     * - Si el curso está NO_INICIADO, el progreso debe ser 0
     * - Si el curso está EN_CURSO, el progreso debe estar entre 1 y 99
     */
    private void validateStatusProgressConsistency(CourseStatus status, int progress) {
        switch (status) {
            case COMPLETADO:
                if (progress != 100) {
                    throw new IllegalArgumentException(
                            "Un curso completado debe tener un progreso del 100%");
                }
                break;
            case NO_INICIADO:
                if (progress != 0) {
                    throw new IllegalArgumentException(
                            "Un curso no iniciado debe tener un progreso del 0%");
                }
                break;
            case EN_CURSO:
                if (progress < 1 || progress > 99) {
                    throw new IllegalArgumentException(
                            "Un curso en curso debe tener un progreso entre 1% y 99%");
                }
                break;
        }
    }
}
