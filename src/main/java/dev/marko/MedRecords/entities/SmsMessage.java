package dev.marko.MedRecords.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sms_messages")
public class SmsMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "from_number", nullable = false)
    private String fromNumber;

    @Column(name = "to_number", nullable = false)
    private String toNumber;

    @Column(name = ("body"), columnDefinition = "TEXT")
    private String body;

    @Column(name = "direction")
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SmsStatus status;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

}
