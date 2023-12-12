package com.example.projectv1.utils;

import java.time.LocalDateTime;

public class TemporaryTokenUtil {
    public static Boolean isResetTokenValid(LocalDateTime expiryTime) {
        return LocalDateTime.now().isBefore(expiryTime);
    }
}
