package com.verein.dto;

import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private String gender;
    private LocalDate membershipDate;
    private MembershipType membershipType;
    private MembershipStatus status;
    private Long clubId;
    private String clubName;
}