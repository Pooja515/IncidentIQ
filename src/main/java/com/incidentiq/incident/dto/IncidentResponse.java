package com.incidentiq.incident.dto;

import com.incidentiq.incident.entity.IncidentSeverity;
import com.incidentiq.incident.entity.IncidentStatus;

import java.time.Instant;

public record IncidentResponse(
        Long id,
        String title,
        String description,
        IncidentSeverity severity,
        IncidentStatus status,
        Instant createdAt
) {
}
