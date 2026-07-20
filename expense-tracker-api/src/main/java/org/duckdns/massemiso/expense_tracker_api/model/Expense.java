package org.duckdns.massemiso.expense_tracker_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "expense")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Expense {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Category category;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @CreatedDate
  private Instant createdDate;

  @LastModifiedDate
  private Instant lastModifiedDate;

  @Builder
  public Expense(String description, Category category, LocalDate date, UserEntity user) {
    this.description = description;
    this.category = category;
    this.date = date;
    this.user = user;
  }

  public void update(String description, Category category, LocalDate date) {
    if (description != null && !description.isBlank()){
      this.description = description;
    }
    if (category != null){
      this.category = category;
    }
    if (date != null){
      this.date = date;
    }
  }
}
