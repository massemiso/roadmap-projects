package org.duckdns.massemiso.todo_list_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String token;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  @Column(nullable = false)
  private LocalDateTime expiryDate;

  @Builder
  public RefreshToken(String token, User user, LocalDateTime expiryDate) {
    this.token = token;
    this.user = user;
    this.expiryDate = expiryDate;
  }
}
