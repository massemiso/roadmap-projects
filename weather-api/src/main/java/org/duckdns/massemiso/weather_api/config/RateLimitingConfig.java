package org.duckdns.massemiso.weather_api.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitingConfig {
  @Value("${bucket4j.rate.limit}")
  private Integer rateLimit;

  @Bean
  public Bucket bucket() {
    Bandwidth limit = Bandwidth.classic(rateLimit, Refill.greedy(rateLimit, Duration.ofMinutes(1)));
    return Bucket4j.builder().addLimit(limit).build();
  }
}
