package com.verein.dto;

import javax.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubRequest {
    @NotBlank(message = "Name ist erforderlich")
    private String name;
    private String description;
    private LocalDate foundedDate;
    private String city;
}