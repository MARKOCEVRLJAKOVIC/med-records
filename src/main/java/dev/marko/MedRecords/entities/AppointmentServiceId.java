package dev.marko.MedRecords.entities;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class AppointmentServiceId implements Serializable {
    private Long appointment;
    private Long service;
}