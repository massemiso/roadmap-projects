package org.duckdns.massemiso.todo_list_api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "todo")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
public class Todo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @CreatedDate private Instant createdAt;
  @LastModifiedDate private Instant modifiedAt;
  private String title;
  private String description;
  private Boolean completed;
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Builder
  public Todo(String title, String description, Boolean completed, User user) {
   this.title = title;
   this.description = description;
   this.completed = completed;
   this.user = user;
  }

  public void update(String title, String description, Boolean completed) {
    if (!title.isBlank()){
      this.title = title;
    }
    if (!description.isBlank()){
      this.description = description;
    }
    this.completed = completed;
  }
}
