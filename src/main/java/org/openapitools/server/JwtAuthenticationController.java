package org.openapitools.server;

import org.openapitools.framework.util.JwtTokenUtil;
import org.openapitools.model.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
//import com.javainuse.service.JwtUserDetailsService;
//import com.javainuse.config.JwtTokenUtil;
//import com.javainuse.model.JwtRequest;
//import com.javainuse.model.JwtResponse;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/9/21
 * <p>Time: 7:46 PM
 *
 * @author Miguel Mu\u00f1oz
 */

@RestController
public class JwtAuthenticationController {
  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationController.class);

  private final AuthenticationManager authenticationManager;

  private final JwtTokenUtil jwtTokenUtil = JwtTokenUtil.getInstance();

  private final JwtUserDetailsService userDetailsService;

  @Autowired
  public JwtAuthenticationController(final AuthenticationManager authenticationManager, final JwtUserDetailsService userDetailsService) {
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    log.debug("Instantiating JwtAuthenticationController");
  }

  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDto authenticationRequest) {
    log.debug("JwtAuthenticationController.createAuthenticationToken");
    
    authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

    final UserDetails userDetails = userDetailsService
        .loadUserByUsername(authenticationRequest.getUsername());

    final String token = jwtTokenUtil.generateToken(userDetails);

    return ResponseEntity.ok(token);
  }

  private void authenticate(String username, String password) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
  }
}
