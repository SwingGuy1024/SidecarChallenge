package org.openapitools.server;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final String ROLE_CLAIM = "role";

  public static JwtTokenUtil getInstance() {
    log.info("Getting JwtTokenUtil instance");
    return instance;
  }
  public static final long JWT_TOKEN_VALIDITY_MILLIS = 5 * 60 * 60 * 1000; // 5 Hours in Milliseconds

  //  @Value("${jwt.secret}") //NON-NLS // I set this in applications.properties, but it didn't work.
  // There has to be a better way to do this. This is not suitable for production!
  private final String secret = "SPEAK FRIEND AND ENTER"; //NON-NLS

  public Claims getAllClaimsFromToken(String token) {
    Jwts.parser().setSigningKey(secret).isSigned(token);
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }
  
  public <T> T getClaimFromToken(String token, String claimName, Class<T> claimClass) {
    final Claims claims = getAllClaimsFromToken(token);
    return claims.get(claimName, claimClass);
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

  /**
   * Retrieve the user role from the jwt token
   * @param token the jwt token
   * @return The role, as a String
   */
  public String getRoleFromToken(String token) {
    return getClaimFromToken(token, ROLE_CLAIM, String.class);
  }
  
  private String doGenerateToken(Map<String, Object> claims, String subject) {
    final long now = System.currentTimeMillis();
    return doGenerateToken(claims, subject, now);
  }
  
  private String doGenerateToken(Map<String, Object> claims, String subject, long issueTimeMillis) {
    return getToken(claims, subject, issueTimeMillis);
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
   * @param username The username
   * @return a valid token
   */
  public String generateToken(String username, String role) {
    Map<String, Object> claims = createDefaultClaimsForUser(role);
    return doGenerateToken(claims, username);
  }

  private Map<String, Object> createDefaultClaimsForUser(final String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(ROLE_CLAIM, role);
    return claims;
  }
  
  /**
   * Validate token
   *
   * @param token The JWT Token
   * @return true if valid, false if the token has expired or tampered with.
   */
  public boolean validateToken(String token) {
    String username;
    Date issuedAt;
    try {
      issuedAt = getClaimFromToken(token, Claims::getIssuedAt);
      username = getUsernameFromToken(token);
    } catch (SignatureException e) {
      log.warn("Invalid token: {}", token, e);
      return false;
    } catch (ExpiredJwtException e) {
      log.warn("Expired token: {}", token, e);
      return false;
    }

    String role = getRoleFromToken(token);
    String duplicateToken = doGenerateToken(createDefaultClaimsForUser(role), username, issuedAt.getTime());
    return duplicateToken.equals(token);
  }
  
  // Package method for testing only

  // Test-only methods with package access only.
  String testOnlyGenerateToken(String username, String role, long millis) {
    Map<String, Object> claims = createDefaultClaimsForUser(role);
    return getToken(claims, username, millis);
  }
  
  String testOnlyGenerateTokenFromTime(String username, String role, long millis) {
    return doGenerateToken(createDefaultClaimsForUser(role), username, millis);
  }
}
