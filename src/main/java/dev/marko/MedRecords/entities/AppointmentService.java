package dev.marko.MedRecords.entities;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "appointment_services")
@IdClass(AppointmentServiceId.class)
public class AppointmentService {

    @Id
    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Id
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "duration_override")
    private Integer durationOverride; // in minutes

    @Column(name = "price_override")
    private BigDecimal priceOverride;
}
