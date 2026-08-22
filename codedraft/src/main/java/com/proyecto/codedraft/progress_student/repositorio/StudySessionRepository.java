package com.proyecto.codedraft.progress_student.repositorio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import com.proyecto.codedraft.progress_student.model.StudySession;

@Repository
public class StudySessionRepository {

    private final ObjectMapper objectMapper;
    private final File studySessionsFile;
    private final Object lock = new Object();

    public StudySessionRepository(ObjectMapper objectMapper,
                                   @Value("${app.data.study-sessions-file:data/study_sessions.json}") String studySessionsFilePath) {
        this.objectMapper = objectMapper;
        this.studySessionsFile = new File(studySessionsFilePath);
    }

    public List<StudySession> findAll() {
        synchronized (lock) {
            if (!studySessionsFile.exists()) {
                return new ArrayList<>();
            }
            List<StudySession> sessions = objectMapper.readValue(studySessionsFile, new TypeReference<List<StudySession>>() {
            });
            return new ArrayList<>(sessions);
        }
    }

    public void saveAll(List<StudySession> sessions) {
        synchronized (lock) {
            File parentDir = studySessionsFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(studySessionsFile, sessions);
        }
    }
}
