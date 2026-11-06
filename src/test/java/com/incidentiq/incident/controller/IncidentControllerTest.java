package com.incidentiq.incident.controller;

import com.incidentiq.incident.entity.Incident;
import com.incidentiq.incident.entity.IncidentSeverity;
import com.incidentiq.incident.entity.IncidentStatus;
import com.incidentiq.incident.service.IncidentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncidentController.class)
class IncidentControllerTest {

    private static final String ENDPOINT = "/api/v1/incidents";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IncidentService incidentService;

    @Test
    void createsIncidentAndReturnsAllResponseFields() throws Exception {
        Instant createdAt = Instant.parse("2026-11-06T12:30:00Z");
        Incident incident = savedIncident(
                42L,
                "Payment service unavailable",
                "Payment API is returning HTTP 500",
                IncidentSeverity.HIGH,
                createdAt
        );
        when(incidentService.createIncident(
                "Payment service unavailable",
                "Payment API is returning HTTP 500",
                IncidentSeverity.HIGH
        )).thenReturn(incident);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Payment service unavailable",
                                  "description": "Payment API is returning HTTP 500",
                                  "severity": "HIGH"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.title").value("Payment service unavailable"))
                .andExpect(jsonPath("$.description").value("Payment API is returning HTTP 500"))
                .andExpect(jsonPath("$.severity").value("HIGH"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));

        verify(incidentService).createIncident(
                "Payment service unavailable",
                "Payment API is returning HTTP 500",
                IncidentSeverity.HIGH
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void rejectsInvalidRequestWithoutCallingService(String requestJson) throws Exception {
        mockMvc.perform(post(ENDPOINT)
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(incidentService);
    }

    static Stream<String> invalidRequests() {
        return Stream.of(
                "{\"description\":\"Valid description\",\"severity\":\"HIGH\"}",
                "{\"title\":\" \",\"description\":\"Valid description\",\"severity\":\"HIGH\"}",
                request("t".repeat(151), "Valid description", "HIGH"),
                "{\"title\":\"Valid title\",\"severity\":\"HIGH\"}",
                request("Valid title", "   ", "HIGH"),
                request("Valid title", "d".repeat(2001), "HIGH"),
                "{\"title\":\"Valid title\",\"description\":\"Valid description\"}",
                request("Valid title", "Valid description", "URGENT")
        );
    }

    @Test
    void acceptsMaximumValidTitleAndDescriptionLengths() throws Exception {
        String title = "t".repeat(150);
        String description = "d".repeat(2000);
        Incident incident = savedIncident(7L, title, description, IncidentSeverity.LOW,
                Instant.parse("2026-11-06T12:30:00Z"));
        when(incidentService.createIncident(title, description, IncidentSeverity.LOW))
                .thenReturn(incident);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(APPLICATION_JSON)
                        .content(request(title, description, "LOW")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.description").value(description));

        verify(incidentService).createIncident(title, description, IncidentSeverity.LOW);
    }

    @Test
    void ignoresClientSuppliedServerControlledFields() throws Exception {
        Instant serverCreatedAt = Instant.parse("2026-11-06T12:30:00Z");
        Incident incident = savedIncident(
                42L, "Valid title", "Valid description", IncidentSeverity.MEDIUM, serverCreatedAt
        );
        when(incidentService.createIncident(
                "Valid title", "Valid description", IncidentSeverity.MEDIUM
        )).thenReturn(incident);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 999,
                                  "title": "Valid title",
                                  "description": "Valid description",
                                  "severity": "MEDIUM",
                                  "status": "CLOSED",
                                  "createdAt": "2000-01-01T00:00:00Z"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.createdAt").value(serverCreatedAt.toString()));

        verify(incidentService).createIncident(
                "Valid title", "Valid description", IncidentSeverity.MEDIUM
        );
    }

    private static Incident savedIncident(
            Long id,
            String title,
            String description,
            IncidentSeverity severity,
            Instant createdAt
    ) {
        Incident incident = mock(Incident.class);
        when(incident.getId()).thenReturn(id);
        when(incident.getTitle()).thenReturn(title);
        when(incident.getDescription()).thenReturn(description);
        when(incident.getSeverity()).thenReturn(severity);
        when(incident.getStatus()).thenReturn(IncidentStatus.OPEN);
        when(incident.getCreatedAt()).thenReturn(createdAt);
        return incident;
    }

    private static String request(String title, String description, String severity) {
        return "{\"title\":\"" + title
                + "\",\"description\":\"" + description
                + "\",\"severity\":\"" + severity + "\"}";
    }
}
