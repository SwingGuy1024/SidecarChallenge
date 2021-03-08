package com.neptunedreams.model;

import java.util.Collection;
import java.util.Collections;
import com.neptunedreams.engine.Role;
import org.springframework.security.core.GrantedAuthority;

/**
 * Class to translate user role, as specified in Role, into a Spring Security GrantedAuthority
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/6/21
 * <p>Time: 10:10 PM
 *
 * @see Role
 * @see GrantedAuthority
 * @author Miguel Mu\u00f1oz
 */
public final class UserAuthority implements GrantedAuthority {
  private final String authority;

  public UserAuthority(Role role) {
    //noinspection HardCodedStringLiteral
    authority = String.format("ROLE_%s", role);
  }

  public UserAuthority(String role) {
    this(Role.fromValue(role));
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
