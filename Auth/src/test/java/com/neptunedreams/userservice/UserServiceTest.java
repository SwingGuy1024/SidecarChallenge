package com.neptunedreams.userservice;

import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neptunedreams.OpenAPI2SpringBoot;
import com.neptunedreams.auth.JwtTokenUtil;
import com.neptunedreams.entity.User;
import com.neptunedreams.exception.BadRequest400Exception;
import com.neptunedreams.exception.Conflict409Exception;
import com.neptunedreams.model.LoginDto;
import com.neptunedreams.model.UserDto;
import com.neptunedreams.repository.UserRepository;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.password.AbstractPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/12/21
 * <p>Time: 2:19 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenAPI2SpringBoot.class)
@Component
public class UserServiceTest {
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;
  
  @Autowired
  private PasswordEncoder encoder;

  @Test(expected = BadRequest400Exception.class)
  public void testMissingEmailAndUsername() {
    UserDto dto = makeUserDto("", "password", "", "1234", "23445");
    UserRepository localMockRepository = mock(UserRepository.class);
    UserService localMockUserService = new UserService(localMockRepository, encoder, new ObjectMapper());
    localMockUserService.createUser(dto, Role.ADMIN);
  }
  
  @Test(expected = BadRequest400Exception.class)
  public void testMissingContactInfo() {
    UserDto dto = makeUserDto("user", "pw", "", "", "1234");
    UserRepository localMockRepository = mock(UserRepository.class);
    UserService localMockUserService = new UserService(localMockRepository, encoder, new ObjectMapper());
    localMockUserService.createUser(dto, Role.ADMIN);
  }
  
  @Test
  public void testMissingUsername() {
    UserDto dto = makeUserDto("", "pw", "user1@nowhere.com", "", "");
    UserRepository localMockRepository = mock(UserRepository.class);
    UserService localMockUserService = new UserService(localMockRepository, encoder, new ObjectMapper());
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    localMockUserService.createUser(dto, Role.ADMIN);
    verify(localMockRepository).save(captor.capture());
    User captured = captor.getValue();
    assertEquals(captured.getUsername(), "user1@nowhere.com");
  }

  @Test(expected = Conflict409Exception.class)
  public void testUsernameConflict() {
    UserDto dto1 = makeUserDto("UserOne", "Pw1", "User1@nowhere.com", "1", "2");
    userService.createUser(dto1, Role.CUSTOMER);
    UserDto dto2 = makeUserDto("UserTwo", "pw2", "user2@nowhere.com", "2", "3");
    userService.createUser(dto2, Role.CUSTOMER);
  }

  @Test(expected = Conflict409Exception.class)
  public void testUsernameConflict2() {
    UserDto dto1 = makeUserDto("UserOne", "Pw1", "User1@nowhere.com", "1", "2");
    userService.createUser(dto1, Role.CUSTOMER);
    UserDto dto2 = makeUserDto("UserTwo", "pw2", "user2@nowhere.com", "3", "1");
    userService.createUser(dto2, Role.CUSTOMER);
  }
  
  @Test
  public void loginUser() {
    UserDto dto1 = makeUserDto("Alpha", "alpha", "alpha@test.test", "11m", "22Land");
    userService.createUser(dto1, Role.CUSTOMER);
    String token1 = userService.loginUser(makeLoginDto(dto1));
    assertTrue(JwtTokenUtil.instance.validateToken(token1));
    assertEquals(Role.CUSTOMER.toString(), JwtTokenUtil.instance.getRoleFromToken(token1));
    
    LoginDto badDto = makeLoginDto("Alpha", "bravo"); 
    try {
      userService.loginUser(badDto);
      fail("Logged in user with bad password");
    } catch (AuthorizationServiceException ignored) { }
    
    LoginDto loginDto2 = makeLoginDto("Bravo", "bravo");
    try {
      userService.loginUser(loginDto2);
      fail("Logged in non-existent user: Bravo");
    } catch (AuthorizationServiceException ignored) { }
    
    // DUPLICATE!
    try {
      userService.createUser(dto1, Role.CUSTOMER);
      fail("Created same user twice!");
    } catch (Conflict409Exception ignored) { }

    UserDto dto3 = makeUserDto("Charlie", "charlie", "", "", "33Land");
    try {
      userService.createUser(dto3, Role.CUSTOMER);
      fail("Created user with no email or mobile phone");
    } catch (BadRequest400Exception ignored) { }
    
    UserDto dto4 = makeUserDto("", "delta", "", "4", "6");
    try {
      userService.createUser(dto4, Role.CUSTOMER);
      fail("Created user with no username or email");
    } catch (BadRequest400Exception ignored) { }

    final String echoEmail = "echo@test.test";
    UserDto dto5 = makeUserDto("", "echo", echoEmail, null, null);
    userService.createUser(dto5, Role.ADMIN);
    LoginDto loginDto5 = makeLoginDto(dto5);
    loginDto5.setUsername(echoEmail);
    String token5 = userService.loginUser(loginDto5);
    String userName5 = JwtTokenUtil.instance.getUsernameFromToken(token5);
    assertEquals(echoEmail, userName5);

    try {
      UserDto dto6 = makeUserDto("Foxtrot", "foxtrot", "foxTrot@test.test", "1m1", "33Land");
      userService.createUser(dto6, Role.CUSTOMER);
    } catch (Conflict409Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testIsRealUser() {
    // User "phantom" doesn't exist.
    User user = userRepository.getOne("Phantom"); // returns a proxy even if no user is found
    assertFalse(userService.isRealUser(user));
    user = userRepository.findByUsername("Phantom"); // Returns null if not found
    assertFalse(userService.isRealUser(user));
  }

  private User makeUser(String username, String password, String email, String mobilePhone, String landPhone) {
    UserDto dto = makeUserDto(username, password, email, mobilePhone, landPhone);
    return objectMapper.convertValue(dto, User.class);
  }

  private UserDto makeUserDto(String username, String password, String email, @Nullable String mobilePhone, @Nullable String landPhone) {
    UserDto dto = new UserDto();
    dto.setUsername(username);
    dto.setPassword(password);
    dto.setEmail(email);
    dto.setMobilePhone(mobilePhone);
    dto.setLandPhone(landPhone);
    return dto;
  }

  private LoginDto makeLoginDto(String username, String pw) {
    LoginDto dto = new LoginDto();
    dto.setUsername(username);
    dto.setPassword(pw);
    return dto;
  }

  private LoginDto makeLoginDto(UserDto userDto) {
    LoginDto dto = new LoginDto();
    dto.setUsername(userDto.getUsername());
    dto.setPassword(userDto.getPassword());
    return dto;
  }

  @Test
  public void testDigitFilter() {
    String s = "1abc2!@#$%^&*()_+3{}|[]“‘«”’»4defghijklmnop5qrstuv6wxyz7ABCDEFGHIJKLMNOP8QRSTUV9WXYZ0,./<>?≤≥\u0660€\u0661‹\u0662›\u0663‡\u0664°\u0665ª·\u0666º‚\u0667¡™£¢∞§¶•\u0668\u0669\u06E0\u06F0\u06F1\u06F2\u06F3\u06F4\u06F5\u06F6\u06F7\u06F8\u06F9";
    String filtered = UserService.removeNonDigits(s);
    String expected = "1234567890\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669\u06F0\u06F1\u06F2\u06F3\u06F4\u06F5\u06F6\u06F7\u06F8\u06F9";
    assertEquals(expected, filtered);
    
    assertNull(UserService.removeNonDigits(null));
  }
  
  @Bean
  PasswordEncoder encoder() {
    return new AbstractPasswordEncoder() {
      @Override
      protected byte[] encode(final CharSequence rawPassword, final byte[] salt) {
        return rawPassword.toString().getBytes(StandardCharsets.UTF_8);
      }

      @Override
      public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        return encodedPassword.contentEquals(rawPassword);
      }
    };
  }
}