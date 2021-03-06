package com.neptunedreams.auth;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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

  private final SignatureAlgorithm HS_512;
  private final Key key;

  JwtTokenUtil() {
    HS_512 = SignatureAlgorithm.HS512;
    // This is fine for a single server, but I wonder if it still works after deploying it in the cloud, where multiple servers can
    // be created, each with its own key.
    key = Keys.secretKeyFor(HS_512);
  }

  public static final long JWT_TOKEN_VALIDITY_MILLIS = 5 * 60 * 60 * 1000; // 5 Hours in Milliseconds

  //  @Value("${jwt.secret}") //NON-NLS // I set this in applications.properties, but it didn't work.
  // There has to be a better way to do this. This is not suitable for production!
//  private final String secret = "SPEAKFRIENDANDENTERSPEAKFRIENDANDENTERSPEAKFRIENDANDENTERSPEAKFRIENDANDENTERSPEAKFRIENDANDENTER"; //NON-NLS
//  private final Key key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");

  public Claims getAllClaimsFromToken(String token) {
    Jwts.parserBuilder().setSigningKey(key).build().isSigned(token);
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
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
    return getToken(claims, subject, now);
  }
  
  private String getToken(final Map<String, Object> claims, final String subject, final long timeMillis) {
    return Jwts
        .builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(timeMillis))
        .setExpiration(new Date(timeMillis + (JWT_TOKEN_VALIDITY_MILLIS)))
        .signWith(key, HS_512).compact();
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
  
  public String generateExpiredTokenForTesting(String username, String role) {
    //noinspection MagicNumber
    long expiredTimeMillis = System.currentTimeMillis() - JWT_TOKEN_VALIDITY_MILLIS - 1000L;
    return getToken(createDefaultClaimsForUser(role), username, expiredTimeMillis);
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
    String duplicateToken = getToken(createDefaultClaimsForUser(role), username, issuedAt.getTime());
    return duplicateToken.equals(token);
  }
  
  // Package method for testing only

  String testOnlyGenerateToken(String username, String role, long millis) {
    return getToken(createDefaultClaimsForUser(role), username, millis);
  }
}
