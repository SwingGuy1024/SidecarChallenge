package org.openapitools.framework.util;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/4/21
 * <p>Time: 5:12 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("Singleton")
//@Component
public enum JwtTokenUtil {
  instance;

  private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);
  
  public static JwtTokenUtil getInstance() {
    log.info("Getting JwtTokenUtil instance");
    return instance;
  }
  public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 5 Hours

//  @Value("${jwt.secret}") //NON-NLS
//  @Value("SPEAK FRIEND AND ENTER") //NON-NLS
  private final String secret = "SPEAK FRIEND AND ENTER"; //NON-NLS

  public Claims getAllClaimsFromToken(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Retrieve username from jwt token
   *
   * @param token The token
   * @return The username
   */
  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  /**
   * Retrieve expiration date from jwt token
   *
   * @param token The token
   * @return The expiration date and time
   */
  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  private boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  private String doGenerateToken(Map<String, Object> claims, String subject) {
//    System.out.printf("Secret = %s%n", secret);
//    System.out.printf("Claims = %s%n", claims); // NON-NLS
//    System.out.printf("Subject = %s%n", subject); // NON-NLS
    final long now = System.currentTimeMillis();
    return Jwts
        .builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + (JWT_TOKEN_VALIDITY * 1000)))
        .signWith(SignatureAlgorithm.HS512, secret).compact();
  }

  /**
   * Generate a token for the user
   *
   * @param userDetails The UserDetails
   * @return a valid token
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return doGenerateToken(claims, userDetails.getUsername());
  }

  /**
   * Validate token
   *
   * @param token       The Token
   * @param userDetails The UserDetails
   * @return true if valid, false if wrong user or token has expired
   */
  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
