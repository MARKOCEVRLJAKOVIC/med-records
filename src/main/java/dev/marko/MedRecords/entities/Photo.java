package dev.marko.MedRecords.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private PhotoType type;

    @Column(name = "taken_at")
    private Timestamp takenAt;

    @JoinColumn(name = "client_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @JoinColumn(name = "medical_record_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MedicalRecord medicalRecord;


}
