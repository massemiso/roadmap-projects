package org.duckdns.massemiso.blogging_platform_api.persistence.repository;

import java.util.List;
import org.duckdns.massemiso.blogging_platform_api.persistence.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
  @Query("SELECT p FROM Post p WHERE " +
      "LOWER(p.title) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
      "LOWER(p.content) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
      "LOWER(p.category) LIKE LOWER(CONCAT('%', :term, '%'))")
  List<Post> findBySearchTerm(@Param("term") String term);}
