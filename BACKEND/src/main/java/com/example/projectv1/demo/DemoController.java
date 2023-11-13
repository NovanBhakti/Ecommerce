package com.example.projectv1.demo;

import com.example.projectv1.config.JwtService;
import com.example.projectv1.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo-controller")
public class DemoController {

    @GetMapping
    public ResponseEntity<String> testLogin(User user){
        return ResponseEntity.ok(user.toString());
    }
}
