package org.openapitools.server;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openapitools.OpenAPI2SpringBoot;
import org.openapitools.model.UserDto;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/15/21
 * <p>Time: 1:16 PM
 *
 * @author Miguel Mu\u00f1oz
 */

@SuppressWarnings("StringConcatenation")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenAPI2SpringBoot.class)
@Component
public class JwtRequestFilterTest {

  private static final String VALID_USER = "validUser";
  private static final String NOT_FOUND = "NotFound";

  @SuppressWarnings("StringConcatenation")
  @Test
  public void testFilterExpired() throws ServletException, IOException {
    // current expiration time is 5 hours, which is 18,000,000 milliseconds
    String user = VALID_USER;
    final long millis = System.currentTimeMillis() - 3_000_000_000L; // longer than a month
    String token = JwtTokenUtil.instance.testOnlyGenerateToken(user, UserDto.RoleEnum.ADMIN.toString(), millis);
    String bearerToken = JwtRequestFilter.BEARER_ + token;
    runTest(user, bearerToken, 1);
  }
  
  @Test
  public void testBadBearer() throws ServletException, IOException {
    String token = "badToken";
    runTest(VALID_USER, token, 0);
  }
  
  @Test
  public void testGoodName() throws ServletException, IOException {
    long millis = System.currentTimeMillis();
    String token = JwtRequestFilter.BEARER_ + JwtTokenUtil.instance.testOnlyGenerateToken(VALID_USER, "ADMIN", millis);
    runTest(VALID_USER, token, 1);
  }

  private void runTest(final String user,  String bearerToken, int contextCount) throws ServletException, IOException {
    UserDetails mockDetails = mock(UserDetails.class);
    when(mockDetails.getUsername()).thenReturn(user);
    
    // I don't like using a raw parameterized class, but this is the only way it compiles. This is only a unit test, so I'm okay with it
    //noinspection rawtypes
    Collection authorityList = Collections.singletonList(fakeAuthority());
    //noinspection unchecked
    when(mockDetails.getAuthorities()).thenReturn(authorityList);

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeaderNames()).thenReturn(new EmptyEnumeration<>());
    when(mockRequest.getHeader(JwtRequestFilter.AUTHORIZATION)).thenReturn(bearerToken);

    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    
    FilterChain mockChain = mock(FilterChain.class);

    SecurityContext mockContext = mock(SecurityContext.class);
    when(mockContext.getAuthentication()).thenReturn(null);

    JwtRequestFilter filter = new JwtRequestFilter();
    filter.setContextSupplierTestOnly(() -> mockContext);

    // Perform test
    filter.doFilterInternal(mockRequest, mockResponse, mockChain);

    verify(mockChain, times(1)).doFilter(any(), any());
    verify(mockContext, times(contextCount)).setAuthentication(any());
  }
  
  private static GrantedAuthority fakeAuthority() {
    return new GrantedAuthority() {
      @Override
      public String getAuthority() {
        return "ADMIN";
      }

      @Override
      public String toString() {
        return getAuthority();
      }
    };
  }

  private static class EmptyEnumeration<T> implements Enumeration<T> {
    @Override
    public boolean hasMoreElements() {
      return false;
    }

    @Override
    public T nextElement() {
      throw new AssertionError("Should never be called!");
    }
  }
}