package org.openapitools.server;


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
  public static final long JWT_TOKEN_VALIDITY_MILLIS = 5 * 60 * 60 * 1000; // 5 Hours in Milliseconds

  //  @Value("${jwt.secret}") //NON-NLS // I set this in applications.properties, but it didn't work.
  // There has to be a better way to do this. This is not suitable for production!
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
    log.trace("Expire at {}", expiration);
    return expiration.before(new Date());
  }

  private String doGenerateToken(Map<String, Object> claims, String subject) {
    final long now = System.currentTimeMillis();
    return getToken(claims, subject, now);
  }

  private String getToken(final Map<String, Object> claims, final String subject, final long timeMillis) {
    return Jwts
        .builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(timeMillis))
        .setExpiration(new Date(timeMillis + (JWT_TOKEN_VALIDITY_MILLIS)))
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

  // Test-only methods with package access only.
  String generateTokenTestOnly(String username, long millis) {
    Map<String, Object> claims = new HashMap<>();
    return getToken(claims, username, millis);
  }
}
