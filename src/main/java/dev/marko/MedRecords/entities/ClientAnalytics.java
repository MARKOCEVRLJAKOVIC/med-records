package dev.marko.MedRecords.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "client_analytics")
@Immutable
public class ClientAnalytics {

    @Id
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "show_rate", precision = 5, scale = 2)
    private BigDecimal showRate;

    @Column(name = "avg_revisit_weeks", precision = 5, scale = 2)
    private BigDecimal avgRevisitWeeks;

    @Column(name = "avg_visit_value", precision = 10, scale = 2)
    private BigDecimal avgVisitValue;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

}