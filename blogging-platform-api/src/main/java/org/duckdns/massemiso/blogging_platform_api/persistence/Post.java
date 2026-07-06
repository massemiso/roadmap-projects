package org.duckdns.massemiso.blogging_platform_api.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "text")
  private String content;

  @Column(nullable = false)
  private String category;

  @NotEmpty(message = "A post must have at least one tag")
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "post_tags",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private List<Tag> tags;

  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  @Builder
  public Post(String  title, String content, String category, List<Tag> tags) {
    this.title = title;
    this.content = content;
    this.category = category;
    this.tags = tags;
  }

  public void update(Post other) {
    if (!other.title.isBlank()){
      this.title = other.title;
    }
    if (!other.content.isBlank()){
      this.content = other.content;
    }
    if (!other.category.isBlank()){
      this.category = other.category;
    }
    if (!other.tags.isEmpty()){
      this.tags = other.tags;
    }
  }
}
