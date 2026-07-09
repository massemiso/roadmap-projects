package org.duckdns.massemiso.todo_list_api.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
  @Value("${jwt.secret}")
  private String secretKey;
  private final long validityInMilliseconds = 3600000; // 1 hour

  public String createToken(String email) {
    return JWT.create()
        .withSubject(email)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + validityInMilliseconds))
        .sign(Algorithm.HMAC256(secretKey));
  }

  public boolean validateToken(String token) {
    try {
      JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String getEmail(String token) {
    return JWT.decode(token).getSubject();
  }

}
