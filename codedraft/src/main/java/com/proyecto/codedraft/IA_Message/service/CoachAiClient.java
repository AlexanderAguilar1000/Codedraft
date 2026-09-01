package com.proyecto.codedraft.IA_Message.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.proyecto.codedraft.IA_Message.dto.CoachMessageRequest;
import com.proyecto.codedraft.IA_Message.dto.CoachMessageResponse;

// Cliente hacia el servicio de IA (Flask). El servicio de IA es una capacidad adicional:
// si no responde a tiempo o falla, este cliente nunca propaga la excepcion hacia arriba,
// para que el registro de una sesion de estudio nunca dependa de que la IA este disponible.
@Service
public class CoachAiClient {

    private static final Logger log = LoggerFactory.getLogger(CoachAiClient.class);

    private final RestClient restClient;

    public CoachAiClient(@Value("${app.ai-service.base-url:http://localhost:5000}") String baseUrl,
                          @Value("${app.ai-service.connect-timeout-ms:2000}") int connectTimeoutMs,
                          @Value("${app.ai-service.read-timeout-ms:8000}") int readTimeoutMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    // Llama a POST /coach-message. Devuelve Optional.empty() si el servicio de IA
    // no esta disponible, responde con error o tarda demasiado.
    public Optional<CoachMessageResponse> requestCoachMessage(CoachMessageRequest request) {
        try {
            CoachMessageResponse response = restClient.post()
                    .uri("/coach-message")
                    .body(request)
                    .retrieve()
                    .body(CoachMessageResponse.class);
            return Optional.ofNullable(response);
        } catch (RestClientException ex) {
            log.warn("El servicio de IA no respondio correctamente, se continua sin feedback del mentor: {}",
                    ex.getMessage());
            return Optional.empty();
        }
    }
}
