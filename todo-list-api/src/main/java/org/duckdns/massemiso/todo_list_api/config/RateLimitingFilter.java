package org.duckdns.massemiso.todo_list_api.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.massemiso.todo_list_api.exception.RateExceededException;
import org.duckdns.massemiso.todo_list_api.exception.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {
  private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

  private Bucket getBucket(String clientId){
    return buckets.computeIfAbsent(clientId, k ->
        Bucket.builder()
            .addLimit(Bandwidth.classic(10, Refill.intervally(1, Duration.ofSeconds(30))))
            .build());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String clientIp = request.getRemoteAddr();
    Bucket bucket = this.getBucket(clientIp);

    log.debug("Trying to consume token from bucket...");
    if (bucket.tryConsume(1)){
      filterChain.doFilter(request, response);
      log.debug("Token consumed");
    } else {
      log.debug("No tokens available in bucket");
      ResponseUtil.writeErrorResponse(
          response,
          HttpStatus.TOO_MANY_REQUESTS,
          new RateExceededException(clientIp),
          "429 TOO MANY REQUESTS: Rate limit exceeded",
          "Rate limit exceeded"
      );
    }
  }
}
