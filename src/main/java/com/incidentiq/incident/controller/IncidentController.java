package com.incidentiq.incident.controller;

import com.incidentiq.incident.dto.CreateIncidentRequest;
import com.incidentiq.incident.dto.IncidentResponse;
import com.incidentiq.incident.entity.Incident;
import com.incidentiq.incident.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(
            @Valid @RequestBody CreateIncidentRequest request
    ) {
        Incident savedIncident = incidentService.createIncident(
                request.title(),
                request.description(),
                request.severity()
        );

        IncidentResponse response = new IncidentResponse(
                savedIncident.getId(),
                savedIncident.getTitle(),
                savedIncident.getDescription(),
                savedIncident.getSeverity(),
                savedIncident.getStatus(),
                savedIncident.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
