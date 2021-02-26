package com.neptunedreams.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import com.neptunedreams.OpenAPI2SpringBoot;
import com.neptunedreams.entity.User;
import com.neptunedreams.framework.exception.BadRequest400Exception;
import com.neptunedreams.framework.exception.Conflict409Exception;
import com.neptunedreams.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
public class UserEngineTest {
  
  private final ObjectMapper objectMapper = new ObjectMapper();
  
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserEngine userEngine;

  @Test(expected = BadRequest400Exception.class)
  public void testMissingEmailAndUsername() {
    com.neptunedreams.model.UserDto dto = makeUserDto("", "password", "", "1234", "23445");
    UserRepository localMockRepository = mock(UserRepository.class);
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    UserEngine localMockUserEngine = new UserEngine(localMockRepository, encoder, new ObjectMapper());
    localMockUserEngine.createUser(dto, Role.ADMIN);
  }
  
  @Test(expected = BadRequest400Exception.class)
  public void testMissingContactInfo() {
    com.neptunedreams.model.UserDto dto = makeUserDto("user", "pw", "", "", "1234");
    UserRepository localMockRepository = mock(UserRepository.class);
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    UserEngine localMockUserEngine = new UserEngine(localMockRepository, encoder, new ObjectMapper());
    localMockUserEngine.createUser(dto, Role.ADMIN);
  }
  
  @Test
  public void testMissingUsername() {
    com.neptunedreams.model.UserDto dto = makeUserDto("", "pw", "user1@nowhere.com", "", "");
    UserRepository localMockRepository = mock(UserRepository.class);
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    UserEngine localMockUserEngine = new UserEngine(localMockRepository, encoder, new ObjectMapper());
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    localMockUserEngine.createUser(dto, Role.ADMIN);
    verify(localMockRepository).save(captor.capture());
    User captured = captor.getValue();
    assertEquals(captured.getUsername(), "user1@nowhere.com");
  }

  @Test(expected = Conflict409Exception.class)
  public void testUsernameConflict() {
    com.neptunedreams.model.UserDto dto1 = makeUserDto("UserOne", "Pw1", "User1@nowhere.com", "1", "2");
    userEngine.createUser(dto1, Role.CUSTOMER);
    com.neptunedreams.model.UserDto dto2 = makeUserDto("UserTwo", "pw2", "user2@nowhere.com", "2", "3");
    userEngine.createUser(dto2, Role.CUSTOMER);
  }

  @Test(expected = Conflict409Exception.class)
  public void testUsernameConflict2() {
    com.neptunedreams.model.UserDto dto1 = makeUserDto("UserOne", "Pw1", "User1@nowhere.com", "1", "2");
    userEngine.createUser(dto1, Role.CUSTOMER);
    com.neptunedreams.model.UserDto dto2 = makeUserDto("UserTwo", "pw2", "user2@nowhere.com", "3", "1");
    userEngine.createUser(dto2, Role.CUSTOMER);
  }

  private User makeUser(String username, String password, String email, String mobilePhone, String landPhone) {
    com.neptunedreams.model.UserDto dto = makeUserDto(username, password, email, mobilePhone, landPhone);
    return objectMapper.convertValue(dto, User.class);
  }

  private com.neptunedreams.model.UserDto makeUserDto(String username, String password, String email, String mobilePhone, String landPhone) {
    com.neptunedreams.model.UserDto dto = new com.neptunedreams.model.UserDto();
    dto.setUsername(username);
    dto.setPassword(password);
    dto.setEmail(email);
    dto.setMobilePhone(mobilePhone);
    dto.setLandPhone(landPhone);
    return dto;
  }
}