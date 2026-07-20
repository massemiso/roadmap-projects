package org.duckdns.massemiso.expense_tracker_api.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("@{jwt.secret}")
  private String secretKey;

  public String createToken(String username){
    return JWT.create()
        .withSubject(username)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() * 3_600_000 )) // 1 hour
        .sign(Algorithm.HMAC256(secretKey));
  }

  public boolean validateToken(String token){
    try{
      JWT
          .require(Algorithm.HMAC256(secretKey))
          .build()
          .verify(token);
      return true;
    } catch(Exception e){
      return false;
    }
  }

  public String getUser(String token) {
    return JWT.decode(token).getSubject();
  }

}
