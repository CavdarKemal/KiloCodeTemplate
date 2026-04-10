package com.verein.dto;

import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {
    @NotBlank(message = "Vorname ist erforderlich")
    private String firstName;

    @NotBlank(message = "Nachname ist erforderlich")
    private String lastName;

    @NotBlank(message = "Email ist erforderlich")
    @Email(message = "Ungültiges Email-Format")
    private String email;

    private String phoneNumber;
    private LocalDate birthDate;
    private String gender;
    private LocalDate membershipDate;

    @NotNull(message = "Mitgliedschaftstyp ist erforderlich")
    private MembershipType membershipType;

    private MembershipStatus status;
    
    @NotNull(message = "Club-ID ist erforderlich")
    private Long clubId;
}