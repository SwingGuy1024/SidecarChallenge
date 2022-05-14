package com.neptunedreams.framework;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.core.type.TypeReference;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import com.neptunedreams.entity.MenuItem;
import com.neptunedreams.entity.User;
import com.neptunedreams.exception.BadRequest400Exception;
import com.neptunedreams.model.UserDto;

import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/9/21
 * <p>Time: 12:34 PM
 *
 * TODO: Move this class to Common. Add test-only DTOs and entities for testing purposes.
 * @author Miguel Mu\u00f1oz
 */
public class PojoUtilityTest {
  @Test(expected = AssertionError.class)
  public void neverNullAssertionTest() {
    // NOTE: This test assumes assertions are turned on during testing. If assertions are off, this test will fail.
    PojoUtility.confirmObjectNeverNull(new MenuItem());
    testIfAssertionsAreOn(); // makes test pass if assertions are off.
  }

  @SuppressWarnings("ErrorNotRethrown")
  private void testIfAssertionsAreOn() {
    try {
      assert false;
    } catch (AssertionError ignore) {
      return;
    }
    System.out.println("Warning: Assertions are off. This test needs assertions to be on.");
    throw new AssertionError("Assertions are off");
  }
  
  @Test
  public void testSkipNull() {
    Iterable<?> iterable = PojoUtility.skipNull(null);
    assertNotNull(iterable);
    Iterator<?> iterator = iterable.iterator();
    while (iterator.hasNext()) {
      fail("Iterable not empty");
    }

    String test = "TEST";
    List<String> list = Collections.singletonList(test);
    iterable = PojoUtility.skipNull(list);
    iterator = iterable.iterator();
    int count = 0;
    while (iterator.hasNext()) {
      assertSame(test, iterator.next());
      count++;
    }
    assertEquals(1, count);
  }

  @Test(expected = BadRequest400Exception.class)
  public void decodeBadInteger() {
    PojoUtility.confirmAndDecodeInteger("bad");
  }

  @Test(expected = BadRequest400Exception.class)
  public void decodeBadLong() {
    PojoUtility.confirmAndDecodeLong("bad");
  }
  
  @Test
  public void testDecodeGood() {
    assertEquals(Integer.valueOf(52), PojoUtility.confirmAndDecodeInteger("52"));
    assertEquals(Integer.valueOf(142857), PojoUtility.confirmAndDecodeInteger("142857"));
    assertEquals(Integer.valueOf(-34), PojoUtility.confirmAndDecodeInteger("-34"));
    assertEquals(Long.valueOf(52), PojoUtility.confirmAndDecodeLong("52"));
    assertEquals(Long.valueOf(-52), PojoUtility.confirmAndDecodeLong("-52"));
    assertEquals(Long.valueOf(142857142857142857L), PojoUtility.confirmAndDecodeLong("142857142857142857"));
    assertEquals(Long.valueOf(-142857142857142857L), PojoUtility.confirmAndDecodeLong("-142857142857142857"));
  }
  
  @Test
  public void testConfirmNeverNull() {
    String text = "0text".substring(1);
    String confirmed = PojoUtility.confirmObjectNeverNull(text);
    assertEquals("1text".substring(1), confirmed);
  }
  
  @Test(expected = BadRequest400Exception.class)
  public void testConfirmNeverNull2() {
    //noinspection unused
    User confirmed = PojoUtility.confirmObjectNeverNull(null);
    fail();
  }

  @Test
  public void testEntityAssertion() {
    try {
      //noinspection unused
      MenuItem confirmed = PojoUtility.confirmObjectNeverNull(new TestClass());
      fail();
    } catch (AssertionError e) {
      System.out.printf("testEntityAssertion: %s%n", e.getMessage());
      MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("MenuItem"));
    }
  }

  @Test
  public void testConfirmNull() {
    PojoUtility.confirmNull(null);
    try {
      PojoUtility.confirmNull("Not null");
      fail("confirmNull failed");
    } catch (BadRequest400Exception ignored) { }
  }

  @Test(expected = BadRequest400Exception.class)
  public void testConfirmEqual() {
    PojoUtility.confirmEqual("this", "that");
  }

  @Test
  public void testConfirmEqual2() {
    PojoUtility.confirmEqual("1this".substring(1), "2this".substring(1));
  }

  @Test
  public void testConfirmEqualMsg() {
    PojoUtility.confirmEqual("unused msg", "1this".substring(1), "2this".substring(1));
    final String msg = "nog wsa wfl rwb xfp";
    try {
      PojoUtility.confirmEqual(msg, "this", "that");
    } catch (BadRequest400Exception e) {
      MatcherAssert.assertThat(e.getMessage(), Matchers.containsString(msg));
    }
  }
  
  @Test
  public void testEmptyIfNull() {
    String s1 = PojoUtility.emptyIfNull("not");
    assertEquals(s1, "not");
    
    String s2 = PojoUtility.emptyIfNull(null);
    assertEquals("", s2);
  }
  
  @Test(expected = BadRequest400Exception.class)
  public void testConfirmNotEmpty1() {
    PojoUtility.confirmNotEmpty(null);
  }

  @Test(expected = BadRequest400Exception.class)
  public void testConfirmNotEmpty2() {
    PojoUtility.confirmNotEmpty("");
  }

  @Test
  public void testConfirmNotEmpty() {
    PojoUtility.confirmNotEmpty("not null");
  }

  @Test
  public void testConvertList() {
    UserDto userDto0 = makeUser("zer", "pw-zer");
    UserDto userDto1 = makeUser("one", "pw-one");
    UserDto userDto2 = makeUser("two", "pw-two");
    List<UserDto> dtoList = Arrays.asList(userDto0, userDto1, userDto2);
    List<User> userList = PojoUtility.convertList(dtoList, new TypeReference<List<User>>() { });
    for (int i=0; i<dtoList.size(); ++i) {
      User user = userList.get(i);
      UserDto dto = dtoList.get(i);
      assertEquals(dto.getUsername(), user.getUsername());
      assertEquals(dto.getPassword(), user.getPassword());
    }
    assertEquals(dtoList.size(), userList.size());
  }
  
  @Test
  public void testAsSet() {
    Set<String> set = PojoUtility.asSet("Red", "White", "Blue");
    MatcherAssert.assertThat(set, Matchers.containsInAnyOrder("Red", "White", "Blue"));
    MatcherAssert.assertThat(set, Matchers.hasSize(3));
  }
  
  private static UserDto makeUser(String name, String pw) {
    UserDto userDto = new UserDto();
    userDto.setUsername(name);
    userDto.setPassword(pw);
    return userDto;
  }

  // For testing entity assertion
  private static class TestClass extends MenuItem {
  }
}