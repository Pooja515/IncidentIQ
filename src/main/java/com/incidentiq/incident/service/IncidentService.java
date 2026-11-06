package com.incidentiq.incident.service;

import com.incidentiq.incident.entity.Incident;
import com.incidentiq.incident.entity.IncidentSeverity;
import com.incidentiq.incident.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    public Incident createIncident(String title, String description, IncidentSeverity severity) {
        Incident incident = new Incident(title, description, severity);
        return incidentRepository.save(incident);
    }
}
