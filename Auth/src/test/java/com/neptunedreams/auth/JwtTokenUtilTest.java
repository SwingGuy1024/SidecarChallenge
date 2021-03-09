package com.neptunedreams.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.Test;
import com.neptunedreams.engine.Role;
import com.neptunedreams.entity.User;

import static com.neptunedreams.auth.JwtTokenUtil.*;
import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/9/21
 * <p>Time: 12:14 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class JwtTokenUtilTest {

  private static final long ONE_HOUR_MILLIS = JWT_TOKEN_VALIDITY_MILLIS/5L;

  @Test
  public void testGenerateToken() {
    JwtTokenUtil jwtTokenUtil = JwtTokenUtil.instance;
//    assertNotNull(jwtTokenUtil.secret);
    User adminUserOne = makeUser("adminUserOne", "passwordOne", Role.ADMIN);
    String tokenOne = jwtTokenUtil.generateToken(adminUserOne.getUsername(), Role.ADMIN.toString());

    User customerUserOne = makeUser("custUserOne", "custPWOne", Role.CUSTOMER);
    String custToken = jwtTokenUtil.generateToken(customerUserOne.getUsername(), Role.CUSTOMER.toString());

    assertTrue(jwtTokenUtil.validateToken(tokenOne));
    assertTrue(jwtTokenUtil.validateToken(custToken));
    System.out.printf("Cust Expiration: %s%n", jwtTokenUtil.getExpirationDateFromToken(custToken)); // NON-NLS
    System.out.printf("Admn Expiration: %s%n", jwtTokenUtil.getExpirationDateFromToken(tokenOne)); // NON-NLS
    System.out.printf("Cust Claims: %s%n", jwtTokenUtil.getAllClaimsFromToken(custToken));
    System.out.printf("Admn Claims: %s%n", jwtTokenUtil.getAllClaimsFromToken(tokenOne)); // NON-NLS
    System.out.printf("Cust Token: %s%n", custToken); // NON-NLS
    System.out.printf("Admn Token: %s%n", tokenOne); // NON-NLS
  }
  
  @Test
  public void testExpiredToken() {
    JwtTokenUtil jwtTokenUtil = JwtTokenUtil.instance;
    User adminUserOne = makeUser("adminUserOne", "passwordOne", Role.ADMIN);
    String expiredToken = jwtTokenUtil.generateExpiredTokenForTesting(adminUserOne.getUsername(), Role.ADMIN.toString());
    assertFalse(jwtTokenUtil.validateToken(expiredToken));
  }
  
  @Test
  public void testBadToken() {
    JwtTokenUtil jwtTokenUtil = JwtTokenUtil.instance;
    User adminUserOne = makeUser("adminUserOne", "passwordOne", Role.ADMIN);
    long oneHourAgo = System.currentTimeMillis() - ONE_HOUR_MILLIS;
    String validToken = jwtTokenUtil.testOnlyGenerateToken(adminUserOne.getUsername(), Role.ADMIN.toString(), oneHourAgo);
    char[] chars = validToken.toCharArray();
    int length = chars.length;
    chars[length-1]++; // change the last character.
    String invalidToken = new String(chars);
    assertFalse(jwtTokenUtil.validateToken(invalidToken));
    assertTrue(jwtTokenUtil.validateToken(validToken));

    String[] parts = validToken.split("\\.");
    
    String part1 = parts[1];
    String imposter = makeImposter(part1);
    System.out.printf("Ch: %s%nTo: %s%n", part1, imposter); // NON-NLS
    String imposterToken = parts[0] + '.' + imposter + '.' + parts[2];
    assertFalse(jwtTokenUtil.validateToken(imposterToken));
  }

  private String makeImposter(String part) {
    Base64.Decoder decoder = Base64.getDecoder();
    String decoded = new String(decoder.decode(part));
    int valueStart = decoded.indexOf(':') + 2;
    final String decodedImposter = decoded.substring(0, valueStart) + '1' + decoded.substring(valueStart);
    System.out.printf("Ch: %s%nTo: %s%n", decoded, decodedImposter);
    final byte[] bytes = decodedImposter.getBytes(StandardCharsets.US_ASCII);

    Base64.Encoder encoder = Base64.getEncoder();
    return new String(encoder.encode(bytes));
  }

  private User makeUser(String name, String password, Role role) {
    User user = new User();
    user.setUsername(name);
    user.setPassword(password);
    user.setRole(role);
    return user;
  }
}