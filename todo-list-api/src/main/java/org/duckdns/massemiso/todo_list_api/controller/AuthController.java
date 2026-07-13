package org.duckdns.massemiso.todo_list_api.controller;

import jakarta.validation.Valid;
import org.duckdns.massemiso.todo_list_api.dto.AuthRequestDto;
import org.duckdns.massemiso.todo_list_api.dto.AuthResponseDto;
import org.duckdns.massemiso.todo_list_api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody AuthRequestDto requestDto){
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(authService.register(requestDto));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto requestDto){
    return ResponseEntity.ok(this.authService.login(requestDto));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponseDto> refresh(@RequestParam String token){
    return ResponseEntity.ok(this.authService.refresh(token));
  }

}
