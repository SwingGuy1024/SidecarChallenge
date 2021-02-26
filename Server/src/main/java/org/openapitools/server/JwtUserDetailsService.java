package org.openapitools.server;

import com.neptunedreams.entity.User;
import com.neptunedreams.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/9/21
 * <p>Time: 7:58 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Component
public class JwtUserDetailsService implements UserDetailsService {
  private static final Logger log = LoggerFactory.getLogger(JwtUserDetailsService.class);

  private final UserRepository userRepository;

  @Autowired
  public JwtUserDetailsService(final UserRepository userRepository) {
    this.userRepository = userRepository;
    log.debug("Instantiating JwtUserDetailsService");
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    log.trace("username: {}", username);
    final User byUsername = userRepository.findByUsername(username);
    if (byUsername == null) {
      throw new UsernameNotFoundException(username);
    }
    if (log.isTraceEnabled()) {
      log.trace("password: {}", byUsername.getPassword());
      log.trace("    Role: {}", byUsername.getRole());
    }
    return byUsername;
  }
}
