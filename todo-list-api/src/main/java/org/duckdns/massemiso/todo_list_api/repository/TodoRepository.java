package org.duckdns.massemiso.todo_list_api.repository;

import java.util.Optional;
import org.duckdns.massemiso.todo_list_api.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {

  Optional<Todo> findByUser_EmailAndId(String email, Long id);

}
