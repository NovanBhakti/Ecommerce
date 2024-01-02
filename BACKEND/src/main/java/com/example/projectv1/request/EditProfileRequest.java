package com.example.projectv1.request;

import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest {
    private String firstName;
    private String lastName;
    private String dobString;
    private String gender;
}
