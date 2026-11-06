package com.incidentiq.incident.service;

import com.incidentiq.incident.entity.Incident;
import com.incidentiq.incident.entity.IncidentSeverity;
import com.incidentiq.incident.entity.IncidentStatus;
import com.incidentiq.incident.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentService incidentService;

    @Test
    void createIncidentSavesIncidentWithProvidedDetailsAndOpenStatus() {
        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        incidentService.createIncident(
                "Payment service unavailable",
                "Payment API is returning HTTP 500",
                IncidentSeverity.HIGH
        );

        ArgumentCaptor<Incident> incidentCaptor = ArgumentCaptor.forClass(Incident.class);
        verify(incidentRepository, times(1)).save(incidentCaptor.capture());

        Incident savedIncident = incidentCaptor.getValue();
        assertEquals("Payment service unavailable", savedIncident.getTitle());
        assertEquals("Payment API is returning HTTP 500", savedIncident.getDescription());
        assertEquals(IncidentSeverity.HIGH, savedIncident.getSeverity());
        assertEquals(IncidentStatus.OPEN, savedIncident.getStatus());
    }
}
