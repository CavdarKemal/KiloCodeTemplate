package com.verein.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberCsvImportRequest {
    
    @NotBlank(message = "Vorname ist erforderlich")
    private String firstName;
    
    @NotBlank(message = "Nachname ist erforderlich")
    private String lastName;
    
    @NotBlank(message = "E-Mail ist erforderlich")
    @Email(message = "Ungültige E-Mail-Adresse")
    private String email;
    
    private String phoneNumber;
    
    private String birthDate;
    
    private String gender;
    
    @NotNull(message = "Mitgliedstyp ist erforderlich")
    private String membershipType;
    
    private String status;
    
    @NotNull(message = "Verein-ID ist erforderlich")
    private Long clubId;
}