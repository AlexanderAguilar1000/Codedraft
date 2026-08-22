package com.proyecto.codedraft.progress_student.servicio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.proyecto.codedraft.course.model.Course;
import com.proyecto.codedraft.course.service.CourseAlreadyCompletedException;
import com.proyecto.codedraft.course.service.CourseService;
import com.proyecto.codedraft.profile.model.Profile;
import com.proyecto.codedraft.profile.service.ProfileService;
import com.proyecto.codedraft.progress_student.dto.StudySessionRequest;
import com.proyecto.codedraft.progress_student.model.StudySession;
import com.proyecto.codedraft.progress_student.repositorio.StudySessionRepository;

@Service
public class StudySessionService {

    // puntos de progreso que otorga cada franja de duracion enviada por el frontend
    private static final int PROGRESS_LESS_THAN_HOUR = 5; // duration = -1
    private static final int PROGRESS_ONE_HOUR = 10;       // duration = 1
    private static final int PROGRESS_MORE_THAN_HOUR = 15; // duration = 2

    private final StudySessionRepository studySessionRepository;
    private final CourseService courseService;
    private final ProfileService profileService;

    public StudySessionService(StudySessionRepository studySessionRepository,
                                CourseService courseService,
                                ProfileService profileService) {
        this.studySessionRepository = studySessionRepository;
        this.courseService = courseService;
        this.profileService = profileService;
    }

    // registra una sesion de estudio: calcula el avance del curso y otorga experiencia al perfil en un solo paso
    public StudySession registerSession(StudySessionRequest request) {
        int progressAdded = resolveProgressAdded(request.getDuration());

        Course course = courseService.getCourseById(request.getCourseId());

        // Validar que el curso no esté completado al 100%
        if (course.getProgress() >= 100) {
            throw new CourseAlreadyCompletedException(
                    "No se puede registrar una sesión de estudio en un curso ya completado al 100%");
        }

        int newProgress = Math.min(100, course.getProgress() + progressAdded);
        System.out.println("Progress"+newProgress);
        courseService.updateProgress(request.getCourseId(), newProgress);

        Profile profile = profileService.addExperiencePoints(progressAdded);

        StudySession session = new StudySession(
                UUID.randomUUID().toString(),
                request.getCourseId(),
                request.getDate(),
                request.getDuration(),
                request.getNotes(),
                progressAdded,
                profile.getExperiencePoints(),
                LocalDateTime.now());

        List<StudySession> sessions = studySessionRepository.findAll();
        if (sessions == null) {
            sessions = new java.util.ArrayList<>();
        }
        sessions.add(session);
        studySessionRepository.saveAll(sessions);

        return session;
    }

    // traduce la franja de duracion (-1, 1, 2) enviada por el frontend a puntos de progreso
    private int resolveProgressAdded(String duration) {
        int durationValue = Integer.parseInt(duration);
        return switch (durationValue) {
            case -1 -> PROGRESS_LESS_THAN_HOUR;
            case 1 -> PROGRESS_ONE_HOUR;
            case 2 -> PROGRESS_MORE_THAN_HOUR;
            default -> throw new IllegalArgumentException(
                    "La duración debe ser -1 (menos de 1 hora), 1 (1 hora) o 2 (más de 1 hora)");
        };
    }
}
