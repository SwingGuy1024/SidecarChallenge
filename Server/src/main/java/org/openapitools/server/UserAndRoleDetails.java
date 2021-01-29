package org.openapitools.server;

import java.util.Collection;
import java.util.Collections;
import org.openapitools.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/28/21
 * <p>Time: 11:37 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public interface UserAndRoleDetails extends UserDetails {
  static Collection<? extends GrantedAuthority> getAuthoritiesFromRole(String role) {
    return Collections.singleton(new User.Authority(role));
  }

}
