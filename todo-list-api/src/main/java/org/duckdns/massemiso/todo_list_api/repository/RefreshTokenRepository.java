package org.duckdns.massemiso.todo_list_api.repository;

import org.duckdns.massemiso.todo_list_api.entity.RefreshToken;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}
