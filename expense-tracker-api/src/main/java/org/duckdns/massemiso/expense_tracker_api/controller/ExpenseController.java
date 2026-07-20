package org.duckdns.massemiso.expense_tracker_api.controller;

import jakarta.validation.Valid;
import java.net.URI;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseFilterDto;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseRequestDto;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseResponseDto;
import org.duckdns.massemiso.expense_tracker_api.service.ExpenseService;
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

  @PostMapping
  public ResponseEntity<ExpenseResponseDto> save(
      @RequestBody @Valid ExpenseRequestDto requestDto){
    ExpenseResponseDto responseDto = this.expenseService.save(requestDto);
    return ResponseEntity
        .created(URI.create("/api/expenses/" + responseDto.id()))
        .body(responseDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExpenseResponseDto> getById(@PathVariable Long id){
    return ResponseEntity
        .ok(this.expenseService.getById(id));
  }

  @GetMapping
  public ResponseEntity<Page<ExpenseResponseDto>> getById(
      @ModelAttribute ExpenseFilterDto expenseFilterDto,
      Pageable pageable){
    return ResponseEntity
        .ok(this.expenseService.getAll(expenseFilterDto, pageable));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ExpenseResponseDto> update(
      @PathVariable Long id,
      @RequestBody @Valid ExpenseRequestDto requestDto){
    return ResponseEntity.ok(this.expenseService.update(id, requestDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id){
    this.expenseService.delete(id);
    return ResponseEntity.noContent().build();
  }

}
