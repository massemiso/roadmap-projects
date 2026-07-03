package org.duckdns.massemiso.weather_api.config.filter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RateLimitingFilter implements Filter {

  private final Bucket bucket;

  @Autowired
  public RateLimitingFilter(Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String path = httpRequest.getRequestURI();
    if (!path.startsWith("/weather")) {
      chain.doFilter(request, response);
      return;
    }

    if (bucket.tryConsume(1)) {
      log.debug("User consumed a token");
      chain.doFilter(request, response);
      return;
    }

    log.warn("User reached rate limit");
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    httpResponse.getWriter().write("Too many requests - please try again later.");
  }
}
