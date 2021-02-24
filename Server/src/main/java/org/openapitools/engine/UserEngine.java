package org.openapitools.engine;

import java.util.function.Function;
import javax.persistence.PersistenceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.entity.User;
import org.openapitools.framework.exception.BadRequest400Exception;
import org.openapitools.framework.exception.Conflict409Exception;
import org.openapitools.model.LoginDto;
import org.openapitools.repositories.UserRepository;
import org.openapitools.server.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.openapitools.engine.PojoUtility.*;

/**
 * Requirements:
 * 1) Email or Username required
 * 2) Email or mobile phone required
 * 3) email is unique
 * 4) username is unique
 * 5) mobile phone and land line are "super-unique"
 *    This means mobile must not match an existing mobile number or land number.
 *    The same is true for the land number
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/12/21
 * <p>Time: 10:27 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@Component
public class UserEngine {
  private static final Logger log = LoggerFactory.getLogger(UserEngine.class);

  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;
  private final PasswordEncoder encoder;

  private static final String USER_PASSWORD_COMBINATION_NOT_FOUND = "User/password Combination not found";

  @Autowired
  public UserEngine(UserRepository userRepository, PasswordEncoder encoder, ObjectMapper objectMapper) {
    this.userRepository = userRepository;
    this.encoder = encoder;
    this.objectMapper = objectMapper;
  }

  /**
   * Create a user with the specified role from the data in the UserDto
   * @param userDto The user details for the new user
   * @param role The role of the new user
   * @return null. This returns Void (which can't be instantiated) rather than void, so it can be used as the
   * supplier to be sent to the ResponseUtility.serve() method.
   */
  public Void createUser(final org.openapitools.model.UserDto userDto, Role role) {
    String username = userDto.getUsername();
    if (userRepository.existsById(username)) {
      throw new Conflict409Exception(String.format("Username %s already exists.", username));
    }

    final String candidateEmail = userDto.getEmail();
    testForExisting(candidateEmail, userRepository::findByEmail, "email");
    if (isBlank(candidateEmail) && isBlank(userDto.getMobilePhone())) {
      throw new BadRequest400Exception("No mobile phone or email");
    }
    if (isBlank(username)) {
      if (isBlank(candidateEmail)) {
        throw new BadRequest400Exception("No username or email");
      } else {
        username = candidateEmail;
        userDto.setUsername(username);
      }
    }
    log.trace("user/email requirements met");
    final String mobilePhone = removeNonDigits(userDto.getMobilePhone());
    userDto.setMobilePhone(mobilePhone);
    final String landPhone = removeNonDigits(userDto.getLandPhone());
    userDto.setLandPhone(landPhone);
    testForExisting(mobilePhone, userRepository::findByLandPhone, "mobile phone as land phone");
    testForExisting(landPhone, userRepository::findByMobilePhone, "land phone as mobile phone");
    log.trace("land/mobile phone requirement met");
    userDto.setPassword(encoder.encode(userDto.getPassword()));
    User newUser = objectMapper.convertValue(userDto, User.class);
    log.trace("Creating user {} with role {}", username, role);
    newUser.setRole(role);
    try {
      userRepository.save(newUser);
    } catch (PersistenceException | DataIntegrityViolationException e) {
      throw new Conflict409Exception(getLastMessage(e), e);
    }
    log.trace("User created");
    return null;
  }
  
  public String loginUser(LoginDto loginDto) {
    log.trace("LoginApiController.loginUser with {}", loginDto);
    log.trace("user DTO: {}", loginDto.getUsername());
    User user = objectMapper.convertValue(loginDto, User.class);
    log.info("user: {} = {}", loginDto.getUsername(), user.getUsername());
    User storedUser = userRepository.findByUsername(user.getUsername());
    log.info("Stored User: {}", storedUser);
    if (storedUser == null) {
      throw new AuthorizationServiceException(USER_PASSWORD_COMBINATION_NOT_FOUND);
    }
    if (!encoder.matches(loginDto.getPassword(), storedUser.getPassword())) {
      throw new AuthorizationServiceException(USER_PASSWORD_COMBINATION_NOT_FOUND);
    }
    final String token = JwtTokenUtil.getInstance().generateToken(user.getUsername(), storedUser.getRole().toString());
    log.info("token = {}", token);
    return token;
  }


  /**
   * Filter out all non-digit characters from a String. I could say s.replaceAll("\\D*", ""), but that only works with
   * Locales that use western numerals. This works with all Locales.
   *
   * @param s The string to strip.
   * @return A string consisting of only numeric digits, which means characters for which Character::isDigit returns true.
   */
  public static String removeNonDigits(String s) {
    if (s == null) {
      return s;
    }
    return s.chars()
        .filter(Character::isDigit)
        // This stream doesn't have a collect() method that takes a Collector!
        .collect(
            StringBuilder::new,                                   // Supplier<StringDigit>
            (stringBuilder, i) -> stringBuilder.append((char) i), // ObjectIntConsumer<StringBuilder>
            (b, b2) -> b.append(b2.toString())                    // BiConsumer<StringBuilder, StringBuilder>
            // The third parameter is only used with spliterators, but it can't be null.
        ).toString();
  }

  private void testForExisting(String candidateValue, Function<String, User> userValueSupplier, String field) {
    if (!isBlank(candidateValue)) {
      if (isRealUser(userValueSupplier.apply(candidateValue))) {
        throw new Conflict409Exception(String.format("%s already in use", field));
      }
    }
  }

  private boolean isRealUser(User candidate) {
    // This is a precaution against a proxy entity that's a stand in for a null value. (Yes, I've seen this happen with Hibernate.) 
    try {
      if (candidate == null) {
        return false;
      }
      //noinspection ResultOfMethodCallIgnored
      candidate.getEmail(); // force an exception for a missing user
    } catch (RuntimeException e) {
      log.debug("Error of {}: {}", e.getClass(), e.getLocalizedMessage());
      return false;
    }
    return true;
  }

}
