package com.example.projectv1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private Integer id;
    private String addressName;
    private String country;
    private String state;
    private String city;
    private String zipCode;
    private String address;
}
