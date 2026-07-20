package org.duckdns.massemiso.expense_tracker_api.controller;

import org.duckdns.massemiso.expense_tracker_api.dto.AuthRequestDto;
import org.duckdns.massemiso.expense_tracker_api.dto.AuthResponseDto;
import org.duckdns.massemiso.expense_tracker_api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService){
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
    return ResponseEntity.ok(this.authService.login(authRequestDto));
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@RequestBody AuthRequestDto authRequestDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.register(authRequestDto));
  }
}
