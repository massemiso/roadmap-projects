package org.duckdns.massemiso.expense_tracker_api.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseFilterDto;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseMapper;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseRequestDto;
import org.duckdns.massemiso.expense_tracker_api.dto.ExpenseResponseDto;
import org.duckdns.massemiso.expense_tracker_api.exception.ExpenseNotFoundException;
import org.duckdns.massemiso.expense_tracker_api.model.Expense;
import org.duckdns.massemiso.expense_tracker_api.model.UserEntity;
import org.duckdns.massemiso.expense_tracker_api.repository.ExpenseRepository;
import org.duckdns.massemiso.expense_tracker_api.repository.ExpenseSpecifications;
import org.duckdns.massemiso.expense_tracker_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final ExpenseMapper expenseMapper;
  private final UserRepository userRepository;

  @Autowired
  public ExpenseService(
      ExpenseRepository expenseRepository,
      ExpenseMapper expenseMapper,
      UserRepository userRepository){
    this.expenseRepository = expenseRepository;
    this.expenseMapper = expenseMapper;
    this.userRepository = userRepository;
  }

  @Transactional
  public ExpenseResponseDto save(ExpenseRequestDto requestDto) {
    log.info("Saving expense request {}", requestDto);

    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity user = userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));

    Expense expense = expenseMapper.toEntity(requestDto, user);
    expense = expenseRepository.save(expense);
    ExpenseResponseDto responseDto = expenseMapper.toDto(expense);

    log.info("Saved expense {}", responseDto);
    return responseDto;
  }

  public ExpenseResponseDto getById(Long id) {
    log.info("Getting expense by id {}", id);

    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Expense expense = expenseRepository
        .findByUser_UsernameAndId(username, id)
        .orElseThrow(() -> new ExpenseNotFoundException(id));
    ExpenseResponseDto responseDto = expenseMapper.toDto(expense);

    log.info("Succesfully got expense {}", responseDto);
    return responseDto;
  }

  public Page<ExpenseResponseDto> getAll(ExpenseFilterDto expenseFilterDto, Pageable pageable) {
    log.info("Getting page of expenses");

    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Specification<Expense> spec = expenseFilterDto.getSpecification();
    spec = spec.and(ExpenseSpecifications.ownedBy(username));

    Page<ExpenseResponseDto> expenses = expenseRepository
        .findAll(spec, pageable)
        .map(expenseMapper::toDto);

    log.info("Found {} expenses", expenses.getTotalElements());
    return expenses;
  }

  @Transactional
  public ExpenseResponseDto update(Long id, ExpenseRequestDto requestDto) {
    log.info("Updating expense {}", requestDto);

    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Expense expense = expenseRepository
        .findByUser_UsernameAndId(username, id)
        .orElseThrow(() -> new ExpenseNotFoundException(id));
    expense.update(requestDto.description(), requestDto.category(), requestDto.date());
    ExpenseResponseDto responseDto = expenseMapper.toDto(expense);

    log.info("Updated expense {}", responseDto);
    return responseDto;
  }

  public void delete(Long id) {
    log.info("Deleting expense {}", id);

    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Expense expense = expenseRepository
        .findByUser_UsernameAndId(username, id)
        .orElseThrow(() -> new ExpenseNotFoundException(id));
    expenseRepository.delete(expense);

    log.info("Deleted expense {}", expense);
  }
}
