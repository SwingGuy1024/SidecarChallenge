package org.openapitools.framework.util;

import java.util.Collections;

import org.junit.Test;
import org.openapitools.entity.User;
import org.openapitools.model.UserDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/9/21
 * <p>Time: 12:14 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class JwtTokenUtilTest {

  @Test
  public void testGenerateToken() {
    JwtTokenUtil jwtTokenUtil = JwtTokenUtil.instance;
//    assertNotNull(jwtTokenUtil.secret);
    User adminUserOne = makeUser("adminUserOne", "passwordOne", UserDto.RoleEnum.ADMIN);
    String tokenOne = jwtTokenUtil.generateToken(adminUserOne);

    User customerUserOne = makeUser("custUserOne", "custPWOne", UserDto.RoleEnum.CUSTOMER);
    String custToken = jwtTokenUtil.generateToken(customerUserOne);

    assertTrue(jwtTokenUtil.validateToken(tokenOne, adminUserOne));
    assertTrue(jwtTokenUtil.validateToken(custToken, customerUserOne));
    assertFalse(jwtTokenUtil.validateToken(tokenOne, customerUserOne));
    assertFalse(jwtTokenUtil.validateToken(custToken, adminUserOne));
    System.out.printf("Cust Expiration: %s%n", jwtTokenUtil.getExpirationDateFromToken(custToken)); // NON-NLS
    System.out.printf("Admn Expiration: %s%n", jwtTokenUtil.getExpirationDateFromToken(tokenOne)); // NON-NLS
    System.out.printf("Cust Claims: %s%n", jwtTokenUtil.getAllClaimsFromToken(custToken));
    System.out.printf("Admn Claims: %s%n", jwtTokenUtil.getAllClaimsFromToken(tokenOne)); // NON-NLS
    System.out.printf("Cust Token: %s%n", custToken); // NON-NLS
    System.out.printf("Admn Token: %s%n", tokenOne); // NON-NLS
  }
  
  private User makeUser(String name, String password, UserDto.RoleEnum role) {
    User user = new User();
    user.setUsername(name);
    user.setPassword(password);
    user.setRole(role);
    return user;
  }
}