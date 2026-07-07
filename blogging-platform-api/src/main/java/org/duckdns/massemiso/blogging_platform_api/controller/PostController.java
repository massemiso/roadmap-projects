package org.duckdns.massemiso.blogging_platform_api.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.blogging_platform_api.dto.PostRequestDto;
import org.duckdns.massemiso.blogging_platform_api.dto.PostResponseDto;
import org.duckdns.massemiso.blogging_platform_api.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@Slf4j
public class PostController {

  @Autowired
  private PostService postService;

  @GetMapping
  public ResponseEntity<List<PostResponseDto>> getAll(
      @RequestParam(required = false, defaultValue = "") String term,
      @RequestParam(required = false, defaultValue = "") String tag){
    log.info("REQUEST: getAll()");
    List<PostResponseDto> dtos = postService.getAll(term, tag);
    log.debug("RESPONSE: getAll() =  {}", dtos);
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PostResponseDto> getById(@PathVariable Long id){
    log.info("REQUEST: getById({})", id);
    PostResponseDto responseDto = postService.getById(id);
    log.debug("RESPONSE: getById({}) =  {}", id, responseDto);
    return ResponseEntity.ok(responseDto);
  }

  @PostMapping
  public ResponseEntity<PostResponseDto> create(@Valid @RequestBody PostRequestDto requestDto){
    log.info("REQUEST: create with arg {}", requestDto);
    PostResponseDto responseDto = postService.create(requestDto);
    log.debug("RESPONSE: create =  {}", responseDto);
    return ResponseEntity.created(URI.create("/post/" + responseDto.id())).body(responseDto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PostResponseDto> update(@PathVariable Long id,
      @Valid @RequestBody PostRequestDto requestDto){
    log.info("REQUEST: update with arg {}", requestDto);
    PostResponseDto responseDto = postService.update(id, requestDto);
    log.debug("RESPONSE: update =  {}", responseDto);
    return ResponseEntity.ok(responseDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id){
    log.info("REQUEST: delete({})", id);
    postService.delete(id);
    log.debug("RESPONSE: delete successful");
    return ResponseEntity.noContent().build();
  }
}
