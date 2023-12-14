package com.example.projectv1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Base64;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String country;
    private String state;
    private String city;
    private String address;
    private String gender;
    private Integer age;
    private String profilePicture;
}
