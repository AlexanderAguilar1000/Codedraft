package com.proyecto.codedraft.course.repositorio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import com.proyecto.codedraft.course.model.Course;

@Repository
public class CourseRepository {

    private final ObjectMapper objectMapper;
    private final File coursesFile;
    private final Object lock = new Object();

    public CourseRepository(ObjectMapper objectMapper,
                             @Value("${app.data.courses-file:data/courses.json}") String coursesFilePath) {
        this.objectMapper = objectMapper;
        this.coursesFile = new File(coursesFilePath);
    }

    public List<Course> findAll() {
        synchronized (lock) {
            if (!coursesFile.exists()) {
                return new ArrayList<>();
            }
            List<Course> courses = objectMapper.readValue(coursesFile, new TypeReference<List<Course>>() {
            });
            return new ArrayList<>(courses);
        }
    }

    public void saveAll(List<Course> courses) {
        synchronized (lock) {
            File parentDir = coursesFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(coursesFile, courses);
        }
    }
}
