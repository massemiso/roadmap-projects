package org.duckdns.massemiso.blogging_platform_api.persistence.repository;

import java.util.Optional;
import org.duckdns.massemiso.blogging_platform_api.persistence.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
  Optional<Tag> findByName(String name);
}
