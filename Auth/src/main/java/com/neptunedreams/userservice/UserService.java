package com.neptunedreams.userservice;

import java.util.function.Function;
import javax.persistence.PersistenceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neptunedreams.auth.JwtTokenUtil;
import com.neptunedreams.entity.User;
import com.neptunedreams.exception.BadRequest400Exception;
import com.neptunedreams.exception.Conflict409Exception;
import com.neptunedreams.model.LoginDto;
import com.neptunedreams.repository.UserRepository;
import org.hibernate.LazyInitializationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.neptunedreams.exception.ExceptionUtils.*;
import static org.apache.logging.log4j.util.Strings.*;

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
@Service
public class UserService {
  private static final @NonNls Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;
  private final PasswordEncoder encoder;

  @SuppressWarnings("HardcodedFileSeparator")
  private static final String USER_PASSWORD_COMBINATION_NOT_FOUND = "User/password Combination not found";

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder encoder, ObjectMapper objectMapper) {
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
  public Void createUser(final com.neptunedreams.model.UserDto userDto, Role role) {
    String username = userDto.getUsername();
    if (userRepository.existsById(username)) {
      throw new Conflict409Exception(String.format("Username %s already exists.", username));
    }

    final String candidateEmail = userDto.getEmail();
    throwConflictIfExists(candidateEmail, userRepository::findByEmail, "email");
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
    throwConflictIfExists(mobilePhone, userRepository::findByLandPhone, "mobile phone as land phone");
    throwConflictIfExists(landPhone, userRepository::findByMobilePhone, "land phone as mobile phone");
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
    final String username = loginDto.getUsername();
    log.trace("user DTO: {}", username);
    User storedUser = userRepository.findByUsername(username);
    log.trace("Stored User: {}", storedUser);
    if (storedUser == null) {
      log.info("Login Reject: {}", username);
      throw new AuthorizationServiceException(USER_PASSWORD_COMBINATION_NOT_FOUND);
    }
    if (!encoder.matches(loginDto.getPassword(), storedUser.getPassword())) {
      log.info("Login password failure: {}", username);
      throw new AuthorizationServiceException(USER_PASSWORD_COMBINATION_NOT_FOUND);
    }
    final Role role = storedUser.getRole();
    final String token = JwtTokenUtil.instance.generateToken(username, role.toString());
    log.info("Login success: {}  Role: {}  Token: {}", username, role, token);
    return token;
  }


  /**
   * Filter out all non-digit characters from a String. I could say s.replaceAll("\\D*", ""), but that only works with
   * Locales that use western numerals. This works with all Locales.
   *
   * @param s The string to strip.
   * @return A string consisting of only numeric digits, which means characters for which Character::isDigit returns true.
   */
  @Contract("null -> null; !null -> !null")
  public static @Nullable String removeNonDigits(@Nullable String s) {
    if (s == null) {
      return null;
    }

    return s.chars()
        .filter(Character::isDigit)
        // This stream doesn't have a collect() method that takes a Collector!
        .collect(
            StringBuilder::new,                 // Supplier<StringDigit>
            UserService::appendAsChar,           // ObjectIntConsumer<StringBuilder>
            (b, b2) -> b.append(b2.toString())  // BiConsumer<StringBuilder, StringBuilder>
        ).toString();
  }

  private static void appendAsChar(StringBuilder stringBuilder, int i) {
    stringBuilder.append((char) i);
  }

  private void throwConflictIfExists(String candidateValue, Function<String, User> userValueSupplier, @NonNls String field) {
    if (!isBlank(candidateValue)) {
      if (isRealUser(userValueSupplier.apply(candidateValue))) {
        throw new Conflict409Exception(String.format("%s already in use", field));
      }
    }
  }

  // This method is a precaution against a proxy entity that's a stand in for a null value. This can happen with Spring Data's 
  // repositories, if the user calls getOne() instead of findById(). Although I don't use getOne in the code, it could creep into the
  // code at some future date, so this is written to account for it. This methods is not private to make it reachable by unit tests.
  boolean isRealUser(User candidate) {
    if (candidate == null) {
      return false;
    }
    try {
      //noinspection ResultOfMethodCallIgnored
      candidate.getEmail(); // force an exception for a missing user
    } catch (LazyInitializationException e) {
      log.debug("Error of {}: {}", e.getClass(), e.getLocalizedMessage());
      return false;
    }
    return true;
  }

}
