package org.example.customrbacjavademo.apps.auth.domain.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class RefreshTokenService {
  @Value("${security.jwt.refresh-secret-key}")
  private String secretKey;

  @Value("${security.jwt.refresh-expiration}")
  private long jwtRefreshExpiration;

  public String generateToken(final UserDetails userDetails) {
    final var extraClaims = new HashMap<String, Object>();

    if (userDetails instanceof UserJpaEntity user) {
      extraClaims.put("id", user.getId());
      extraClaims.put("name", user.getName());
      extraClaims.put("roleId", user.getRole().getId());
    }

    return generateToken(extraClaims, userDetails);
  }

  public boolean isTokenValid(final String refreshToken) {
    try {
      return !isTokenExpired(refreshToken);
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isTokenExpired(final String token) {
    final Date expiration = extractExpiration(token);
    return expiration.before(new Date());
  }

  private Date extractExpiration(final String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public String generateToken(final Map<String, Object> extraClaims, final UserDetails userDetails) {
    return buildToken(extraClaims, userDetails, jwtRefreshExpiration);
  }

  public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
    final var claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private String buildToken(
      final Map<String, Object> extraClaims,
      UserDetails userDetails,
      final long expiration
  ) {
    return Jwts
        .builder()
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignInKey())
        .compact();
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts
          .parser()
          .verifyWith(getSignInKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  private SecretKey getSignInKey() {
    final var keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
