package dev.marko.MedRecords.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "appointment_notifications")
public class AppointmentNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(name = "status", nullable = false)
    private String status; // PENDING / SENT / FAILED

    @Column(name = "notification_type")
    private String notificationType; // EMAIL / SMS / PUSH

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}