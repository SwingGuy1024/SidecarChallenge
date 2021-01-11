package org.openapitools.server;

import org.openapitools.entity.User;
import org.openapitools.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/9/21
 * <p>Time: 7:58 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
  private static final Logger log = LoggerFactory.getLogger(JwtUserDetailsService.class);

  private final UserRepository userRepository;

  private final PasswordEncoder encoder = new BCryptPasswordEncoder();

  @Autowired
  public JwtUserDetailsService(final UserRepository userRepository) {
    this.userRepository = userRepository;
    log.debug("Instantiating JwtUserDetailsService");
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    log.debug("username: {}", username);
    final User byUsername = userRepository.findByUsername(username);
    if (byUsername == null) {
      throw new UsernameNotFoundException(username);
    }
    log.debug("password: {}", byUsername.getPassword());
    log.debug("    Role: {}", byUsername.getRole());
    log.debug("{}: {}", username, encoder.encode(username));
    return byUsername;
  }
}
