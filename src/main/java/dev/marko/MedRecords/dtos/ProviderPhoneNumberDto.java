package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.Provider;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
public class ProviderPhoneNumberDto {

    private Long id;
    private String phoneNumber;
    private Timestamp createdAt;
    private Long providerId;

}
