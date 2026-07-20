package org.duckdns.massemiso.expense_tracker_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "sec_user")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private String username;
  @Column(nullable = false)
  private String password;
  @CreatedDate
  private Instant createdDate;
  @LastModifiedDate
  private Instant lastModifiedDate;

  @Builder
  public UserEntity(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public String toString() {
    return "UserEntity{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", password='[MASKED]" + '\'' +
        ", createdDate=" + createdDate +
        ", lastModifiedDate=" + lastModifiedDate +
        '}';
  }
}
