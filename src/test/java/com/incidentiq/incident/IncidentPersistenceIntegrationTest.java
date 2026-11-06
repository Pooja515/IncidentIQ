package com.incidentiq.incident;

import com.incidentiq.incident.entity.Incident;
import com.incidentiq.incident.entity.IncidentSeverity;
import com.incidentiq.incident.entity.IncidentStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "DB_URL=jdbc:postgresql://localhost:5432/incidentiq_test",
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@Transactional
@EnabledIfEnvironmentVariable(named = "RUN_POSTGRES_INTEGRATION", matches = "true")
class IncidentPersistenceIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void flywayMigrationAndJpaMappingPersistAnIncident() {
        Incident incident = new Incident(
                "Payment service unavailable",
                "Payment API is returning HTTP 500",
                IncidentSeverity.HIGH
        );

        entityManager.persist(incident);
        entityManager.flush();

        assertNotNull(incident.getId());
        assertEquals(IncidentStatus.OPEN, incident.getStatus());
        assertNotNull(incident.getCreatedAt());

        entityManager.clear();
        Incident persistedIncident = entityManager.find(Incident.class, incident.getId());

        assertNotNull(persistedIncident);
        assertEquals("Payment service unavailable", persistedIncident.getTitle());
        assertEquals("Payment API is returning HTTP 500", persistedIncident.getDescription());
        assertEquals(IncidentSeverity.HIGH, persistedIncident.getSeverity());
        assertEquals(IncidentStatus.OPEN, persistedIncident.getStatus());
        assertNotNull(persistedIncident.getCreatedAt());
    }
}
