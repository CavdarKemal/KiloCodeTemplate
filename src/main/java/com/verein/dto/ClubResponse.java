package com.verein.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate foundedDate;
    private String city;
    private int memberCount;
}