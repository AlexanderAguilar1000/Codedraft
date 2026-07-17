package com.proyecto.codedraft.course.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.proyecto.codedraft.course.dto.CourseRequest;
import com.proyecto.codedraft.course.model.Course;
import com.proyecto.codedraft.course.model.CoursePriority;
import com.proyecto.codedraft.course.model.CourseStatus;
import com.proyecto.codedraft.course.repositorio.CourseRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course registerCourse(CourseRequest request) {

        //recibe el curso  y valida que tenga el nombre del curso 
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("El nombre del curso es obligatorio");
        }


        
        CourseStatus status = StringUtils.hasText(request.getStatus())
                ? parseStatus(request.getStatus())//obtener el estado 
                : CourseStatus.NO_INICIADO;
        CoursePriority priority = StringUtils.hasText(request.getPriority())
                ? parsePriority(request.getPriority()) //obtener la prioridad 
                : CoursePriority.MEDIA;
        LocalDate targetDate = StringUtils.hasText(request.getTargetDate())
                ? parseTargetDate(request.getTargetDate()) //obtener la fecha objetivo 
                : null;
        int progress = request.getProgress() != null ? request.getProgress() : 0;

        Course course = new Course(UUID.randomUUID().toString(), request.getName(), request.getDescription(),
                status, priority, targetDate, progress);

        List<Course> courses = courseRepository.findAll();
        courses.add(course);//lo agrega a la lista de cursos 
        courseRepository.saveAll(courses);// lo guarda en la base de datos 
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
}
