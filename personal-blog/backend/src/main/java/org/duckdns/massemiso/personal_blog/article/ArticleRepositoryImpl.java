package org.duckdns.massemiso.personal_blog.article;

import jakarta.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

@Component
@Slf4j
public class ArticleRepositoryImpl implements ArticleRepository {

  private final Path PATH;

  @Autowired
  public ArticleRepositoryImpl(@Value("${blog.storage.path}") String pathName) {
    this.PATH = Paths.get(pathName);
  }

  @Override
  public List<Article> getAll() {
    try {
      return this.readAllFiles();
    } catch (IOException e) {
      log.error("Can't read article files", e);
      return List.of();
    }
  }

  @Override
  public Optional<Article> getById(Long id) {
    try {
      return this.readFile(id);
    } catch (IOException | RuntimeException e) {
      log.error("Can't read article file", e);
    }
    return Optional.empty();
  }

  @Override
  public Article save(Article article) {
    if (article.getId() == null){
      try {
        article.setId(this.generateId());
      } catch (IOException e) {
        log.error("Can't generate new id file", e);
        throw new RuntimeException("Failed to save article");
      }
    }

    try {
      this.writeFile(article);
    } catch (IOException e) {
      log.error("Can't write new article file", e);
      throw new RuntimeException("Failed to write article file");
    }
    return article;
  }

  @Override
  public Article update(Article article) {
    try {
      this.updateFile(article);
    } catch (IOException e) {
      log.error("Can't update article file", e);
      throw new RuntimeException("Failed to update article file");
    }
    return article;
  }

  @Override
  public void delete(Long id) {
    try {
      this.deleteFile(this.getPathFile(id));
    } catch (IOException e) {
      log.error("Can't delete article file", e);
      throw new RuntimeException("Failed to delete article file");
    }
  }

  private synchronized void deleteFile(Path filePath) throws IOException {
    if (!Files.exists(filePath)) {
      return;
    }
    Files.delete(filePath);
  }

  private synchronized void updateFile(Article article) throws IOException {
    String json = this.toJson(article);
    if (!Files.exists(PATH)) {
      Files.createDirectory(PATH);
    }
    Path filePath = this.getPathFile(article.getId());
    Path tempPath = this.getPathTempFile(article.getId());
    try (BufferedWriter bw = Files.newBufferedWriter(tempPath)) {
      bw.write(json);
      Files.move(
          tempPath, filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  private synchronized void writeFile(Article article) throws IOException {
    String json = this.toJson(article);
    if (!Files.exists(PATH)) {
      Files.createDirectory(PATH);
    }
    Path filePath = this.getPathFile(article.getId());
    try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
      bw.write(json);
    }
  }

  private List<Article> readAllFiles() throws IOException {
    List<Article> articles = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(PATH)) {
      List<String> ids =
          paths
              .map(p -> p.getFileName().toString())
              .filter(s -> s.endsWith(".json"))
              .map(s -> s.replaceAll(".json", ""))
              .toList();
      for (String id : ids) {
        this.readFile(Long.valueOf(id)).ifPresent(articles::add);
      }
    }

    articles.sort((i, j) -> j.getDateOfPublication().compareTo(i.getDateOfPublication()));
    return articles;
  }

  private Optional<Article> readFile(Long id) throws IOException {
    Path filePath = this.getPathFile(id);
    if (!Files.exists(filePath)) {
      log.error("File {} doesn't exist", filePath);
      return Optional.empty();
    }
    String json = Files.readString(filePath);
    return Optional.of(this.toObject(json));
  }

  private String toJson(Article article) {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    return ow.writeValueAsString(article);
  }

  private Article toObject(String json) {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(json, Article.class);
  }

  private synchronized Long generateId() throws IOException {
    if (!Files.exists(PATH)) {
      Files.createDirectory(PATH);
    }

    Path idPath = PATH.resolve("last_id.txt");

    // 1. Read existing ID
    long currentId = 0L;
    if (Files.exists(idPath)) {
      String content = Files.readString(idPath).trim();
      if (!content.isEmpty()) {
        currentId = Long.parseLong(content);
      }
    }

    // 2. Increment
    long nextId = currentId + 1;

    // 3. Write new ID back
    Files.writeString(idPath, String.valueOf(nextId));

    return nextId;
  }

  private Path getPathFile(Long id) {
    return PATH.resolve(id + ".json");
  }

  private Path getPathTempFile(Long id) {
    return PATH.resolve(id + "_temp.json");
  }
}
