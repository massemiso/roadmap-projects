package org.duckdns.massemiso.blogging_platform_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BloggingPlatformApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BloggingPlatformApiApplication.class, args);
	}

}
