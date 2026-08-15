package com.proyecto.codedraft.course.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import com.proyecto.codedraft.course.dto.CourseRequest;
import com.proyecto.codedraft.course.service.CourseService;

/**
 * TC-NEG-001: Registrar curso sin campo `name`.
 * Ver src/main/java/com/proyecto/codedraft/docs/test_cases_register_curso.md
 */
@WebMvcTest(CursoController.class)
class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;

    @Test
    void registerCourseShouldReturnBadRequestWhenNameIsMissing() throws Exception {
        CourseRequest request = new CourseRequest();
        request.setTargetDate(java.time.LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/courses/registerCurso")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("El nombre del curso es obligatorio")));

        verify(courseService, never()).registerCourse(any(CourseRequest.class));
    }
}
