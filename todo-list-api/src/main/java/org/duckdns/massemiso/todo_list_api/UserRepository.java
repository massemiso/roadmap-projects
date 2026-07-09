package org.duckdns.massemiso.todo_list_api;

import java.util.Optional;
import org.duckdns.massemiso.todo_list_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

}
