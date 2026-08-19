package com.proyecto.codedraft.course.repositorio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import com.proyecto.codedraft.course.model.CatalogCourse;

/**
 * Repositorio para leer el catálogo estático de cursos sugeridos
 * desde catalog.json (solo lectura).
 */
@Repository
public class CatalogRepository {

    private final ObjectMapper objectMapper;
    private final File catalogFile;
    private final Object lock = new Object();

    public CatalogRepository(ObjectMapper objectMapper,
                              @Value("${app.data.catalog-file:data/catalog.json}") String catalogFilePath) {
        this.objectMapper = objectMapper;
        this.catalogFile = new File(catalogFilePath);
    }

    public List<CatalogCourse> findAll() {
        synchronized (lock) {
            if (!catalogFile.exists()) {
                return new ArrayList<>();
            }
            List<CatalogCourse> courses = objectMapper.readValue(catalogFile, new TypeReference<List<CatalogCourse>>() {
            });
            return new ArrayList<>(courses);
        }
    }

    public void deleteById(String courseId) {
        synchronized (lock) {
            if (!catalogFile.exists()) {
                throw new IllegalStateException("El archivo de catálogo no existe");
            }
            List<CatalogCourse> courses = objectMapper.readValue(catalogFile, new TypeReference<List<CatalogCourse>>() {
            });
            boolean removed = courses.removeIf(course -> course.getId().equals(courseId));
            if (!removed) {
                throw new IllegalArgumentException("Curso con ID " + courseId + " no encontrado");
            }
            objectMapper.writeValue(catalogFile, courses);
        }
    }

    public void save(CatalogCourse course) {
        synchronized (lock) {
            List<CatalogCourse> courses;
            if (!catalogFile.exists()) {
                courses = new ArrayList<>();
            } else {
                courses = objectMapper.readValue(catalogFile, new TypeReference<List<CatalogCourse>>() {
                });
            }
            courses.add(course);
            objectMapper.writeValue(catalogFile, courses);
        }
    }
}
