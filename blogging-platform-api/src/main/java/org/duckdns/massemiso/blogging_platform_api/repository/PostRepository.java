package org.duckdns.massemiso.blogging_platform_api.repository;

import org.duckdns.massemiso.blogging_platform_api.persistence.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
