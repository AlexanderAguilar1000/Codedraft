package com.proyecto.codedraft.progress_student.servicio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.proyecto.codedraft.IA_Message.dto.CoachCourseContext;
import com.proyecto.codedraft.IA_Message.dto.CoachMessageRequest;
import com.proyecto.codedraft.IA_Message.dto.CoachMessageResponse;
import com.proyecto.codedraft.IA_Message.dto.CoachStudySessionContext;
import com.proyecto.codedraft.IA_Message.dto.CoachUserContext;
import com.proyecto.codedraft.IA_Message.service.CoachAiClient;
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

    // minutos aproximados por franja, solo como contexto informativo para el Mentor IA
    private static final int MINUTES_LESS_THAN_HOUR = 30;
    private static final int MINUTES_ONE_HOUR = 60;
    private static final int MINUTES_MORE_THAN_HOUR = 90;

    private final StudySessionRepository studySessionRepository;
    private final CourseService courseService;
    private final ProfileService profileService;
    private final CoachAiClient coachAiClient;

    public StudySessionService(StudySessionRepository studySessionRepository,
                                CourseService courseService,
                                ProfileService profileService,
                                CoachAiClient coachAiClient) {
        this.studySessionRepository = studySessionRepository;
        this.courseService = courseService;
        this.profileService = profileService;
        this.coachAiClient = coachAiClient;
    }

    // registra una sesion de estudio: calcula el avance del curso, otorga experiencia al perfil
    // y solicita feedback al Mentor IA (si el servicio de IA falla, la sesion se guarda igual)
    public StudySession registerSession(StudySessionRequest request) {
        int duration = Integer.parseInt(request.getDuration());
        int progressAdded = resolveProgressAdded(duration);

        Course course = courseService.getCourseById(request.getCourseId());

        // Validar que el curso no esté completado al 100%
        if (course.getProgress() >= 100) {
            throw new CourseAlreadyCompletedException(
                    "No se puede registrar una sesión de estudio en un curso ya completado al 100%");
        }

        int newProgress = Math.min(100, course.getProgress() + progressAdded);
        courseService.updateProgress(request.getCourseId(), newProgress);

        Profile profile = profileService.addExperiencePoints(progressAdded);

        Optional<CoachMessageResponse> mentorResponse =
                requestMentorFeedback(profile, course, newProgress, duration, request.getNotes());

        StudySession session = new StudySession(
                UUID.randomUUID().toString(),
                request.getCourseId(),
                request.getDate(),
                duration,
                request.getNotes(),
                progressAdded,
                profile.getExperiencePoints(),
                LocalDateTime.now(),
                mentorResponse.map(CoachMessageResponse::getCharacter).orElse(null),
                mentorResponse.map(CoachMessageResponse::isValid).orElse(null),
                mentorResponse.map(CoachMessageResponse::getMessage).orElse(null),
                mentorResponse.map(CoachMessageResponse::getWhyItMatters).orElse(null),
                mentorResponse.map(CoachMessageResponse::getRealWorldUse).orElse(null),
                mentorResponse.map(CoachMessageResponse::getChallenge).orElse(null));

        List<StudySession> sessions = studySessionRepository.findAll();
        sessions.add(session);
        studySessionRepository.saveAll(sessions);

        return session;
    }

    // arma el contexto para el Mentor IA (perfil, curso y progreso ya actualizado) y le pide feedback;
    // nunca lanza excepcion, si el servicio de IA falla devuelve Optional.empty()
    private Optional<CoachMessageResponse> requestMentorFeedback(Profile profile, Course course, int progress,
                                                                   int duration, String notes) {
        CoachUserContext userContext =
                new CoachUserContext(profile.getRol(), profile.getCarrera(), profile.getIntereses());
        CoachCourseContext courseContext = new CoachCourseContext(course.getName(), course.getDescription());
        CoachStudySessionContext studySessionContext =
                new CoachStudySessionContext(resolveApproximateMinutes(duration), notes);

        CoachMessageRequest coachRequest =
                new CoachMessageRequest(userContext, courseContext, progress, studySessionContext);
        return coachAiClient.requestCoachMessage(coachRequest);
    }

    // traduce la franja de duracion (-1, 1, 2) enviada por el frontend a puntos de progreso
    private int resolveProgressAdded(int duration) {
        return switch (duration) {
            case -1 -> PROGRESS_LESS_THAN_HOUR;
            case 1 -> PROGRESS_ONE_HOUR;
            case 2 -> PROGRESS_MORE_THAN_HOUR;
            default -> throw new IllegalArgumentException(
                    "La duración debe ser -1 (menos de 1 hora), 1 (1 hora) o 2 (más de 1 hora)");
        };
    }

    // traduce la franja de duracion a minutos aproximados, solo como contexto para el Mentor IA
    private int resolveApproximateMinutes(int duration) {
        return switch (duration) {
            case -1 -> MINUTES_LESS_THAN_HOUR;
            case 1 -> MINUTES_ONE_HOUR;
            case 2 -> MINUTES_MORE_THAN_HOUR;
            default -> 0;
        };
    }
}
