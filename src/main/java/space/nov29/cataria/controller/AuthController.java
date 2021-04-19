package space.nov29.cataria.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.nov29.cataria.dto.LoginRequest;
import space.nov29.cataria.dto.RegisterRequest;
import space.nov29.cataria.service.AuthService;

@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/editorLogin")
    public String login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
