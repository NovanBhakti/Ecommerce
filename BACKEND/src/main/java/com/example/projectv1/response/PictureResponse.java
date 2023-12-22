package com.example.projectv1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PictureResponse {
    private UUID pictureId;
    private UUID entityId;
    private String file;
}
