package org.duckdns.massemiso.expense_tracker_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseFilterDto;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseRequestDto;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseResponseDto;
import org.duckdns.massemiso.expense_tracker_api.service.ExpenseService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

  private final ExpenseService expenseService;

  @Autowired
  public ExpenseController(ExpenseService expenseService){
    this.expenseService = expenseService;
  }

  @Operation(
      summary = "Create a new expense for user, needs authentication"
  )
  @ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @ApiResponse(
      responseCode = "201",
      description = "Expense created successfully"
  )
  @ApiResponse(
      responseCode = "400",
      description = "Parameter not valid",
      content = @Content
  )
  @PostMapping
  public ResponseEntity<ExpenseResponseDto> save(
      @RequestBody @Valid ExpenseRequestDto requestDto){
    ExpenseResponseDto responseDto = this.expenseService.save(requestDto);
    return ResponseEntity
        .created(URI.create("/api/expenses/" + responseDto.id()))
        .body(responseDto);
  }

  @Operation(
      summary = "Get expense by id, needs authentication"
  )
  @ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @ApiResponse(
      responseCode = "200",
      description = "Expense retrieved successfully"
  )
  @ApiResponse(
      responseCode = "404",
      description = "Not found expense",
      content = @Content
  )
  @GetMapping("/{id}")
  public ResponseEntity<ExpenseResponseDto> getById(@PathVariable Long id){
    return ResponseEntity
        .ok(this.expenseService.getById(id));
  }

  @Operation(
      summary = "Get a page of user's expenses, needs authentication"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Get page of user's expenses successful",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @GetMapping
  public ResponseEntity<Page<ExpenseResponseDto>> getAll(
      @ModelAttribute ExpenseFilterDto expenseFilterDto,
      @ParameterObject
      @Schema(
          example = """
                  {
                    "page": 0,
                    "size": 20,
                    "sort": "name,asc"
                  }
                  """
      )
      Pageable pageable){
    return ResponseEntity
        .ok(this.expenseService.getAll(expenseFilterDto, pageable));
  }

  @Operation(
      summary = "Updates an existing expense of user, needs authentication"
  )
  @ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @ApiResponse(
      responseCode = "200",
      description = "Expense updated successfully"
  )
  @ApiResponse(
      responseCode = "400",
      description = "Parameter not valid",
      content = @Content
  )
  @ApiResponse(
      responseCode = "404",
      description = "Expense not found",
      content = @Content
  )
  @PutMapping("/{id}")
  public ResponseEntity<ExpenseResponseDto> update(
      @PathVariable Long id,
      @RequestBody @Valid ExpenseRequestDto requestDto){
    return ResponseEntity.ok(this.expenseService.update(id, requestDto));
  }

  @Operation(
      summary = "Deletes an existing expense of user, needs authentication"
  )
  @ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @ApiResponse(
      responseCode = "204",
      description = "Expense deleted successfully"
  )
  @ApiResponse(
      responseCode = "404",
      description = "Expense not found",
      content = @Content
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id){
    this.expenseService.delete(id);
    return ResponseEntity.noContent().build();
  }

}
