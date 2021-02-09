package org.openapitools.model;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;

/**
 * Class to translate user role, as specified in UserDto.RoleEnum, into a Spring Security GrantedAuthority
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/6/21
 * <p>Time: 10:10 PM
 *
 * @see UserDto.RoleEnum
 * @see GrantedAuthority
 * @author Miguel Mu\u00f1oz
 */
public final class UserAuthority implements GrantedAuthority {
  private final String authority;

  public UserAuthority(UserDto.RoleEnum role) {
    //noinspection HardCodedStringLiteral
    authority = String.format("ROLE_%s", role);
  }

  public UserAuthority(String role) {
    this(UserDto.RoleEnum.fromValue(role));
  }

  @Override
  public String getAuthority() {
    return authority;
  }

  @Override
  public String toString() {
    return authority;
  }

  public static Collection<? extends GrantedAuthority> getAuthoritiesFromRole(String role) {
    return Collections.singleton(new UserAuthority(role));
  }
}
