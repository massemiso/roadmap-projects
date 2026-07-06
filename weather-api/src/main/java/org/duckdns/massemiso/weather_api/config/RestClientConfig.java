package org.duckdns.massemiso.weather_api.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Value("${restclient.connect.timeout}")
  private Integer connectTimeout;
  @Value("${restclient.read.timeout}")
  private Integer readTimeout;

  @Bean
  public RestClient.Builder restClientBuilder() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(Duration.ofSeconds(connectTimeout * 1000L));
    factory.setReadTimeout(Duration.ofSeconds(readTimeout * 1000L));
    return RestClient.builder().requestFactory(factory);
  }
}

