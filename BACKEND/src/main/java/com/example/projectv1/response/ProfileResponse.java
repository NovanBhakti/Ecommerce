package com.example.projectv1.response;

import com.example.projectv1.entity.Role;
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
    private String gender;
    private Integer age;
    private String profilePicture;
    private Role role;
}
