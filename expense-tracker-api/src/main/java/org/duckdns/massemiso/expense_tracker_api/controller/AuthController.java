package org.duckdns.massemiso.expense_tracker_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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

  @Operation(summary = "Login user, returns token valid for 30 minutes")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Login successful")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "400",
      description = "Parameter not valid",
      content = @Content)
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Wrong password",
      content = @Content)
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "404",
      description = "User not found",
      content = @Content)
  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthRequestDto authRequestDto) {
    return ResponseEntity.ok(this.authService.login(authRequestDto));
  }

  @Operation(
      summary = "Register new user, returns token valid for 1 hour",
      description = "Register with username and password"
  )
  @ApiResponse(
      responseCode = "201",
      description = "Register successful"
  )
  @ApiResponse(
      responseCode = "400",
      description = "Parameter not valid"
  )
  @ApiResponse(
      responseCode = "409",
      description = "User already exists"
  )
  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid AuthRequestDto authRequestDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.register(authRequestDto));
  }
}
