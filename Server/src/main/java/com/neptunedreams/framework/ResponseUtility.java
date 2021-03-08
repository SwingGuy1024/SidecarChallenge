package com.neptunedreams.framework;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import com.neptunedreams.exception.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/19/18
 * <p>Time: 11:46 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public enum ResponseUtility {
  ;
  private static final Logger log = LoggerFactory.getLogger(ResponseUtility.class);

  /**
   * Serves a method to create an entity, using HttpStatus.CREATED as the response if successful. This method delegates the work to 
   * serve(), but also translates the returned integer into a String. This is designed for endpoints that create an entity and return 
   * its id. Unlike the other {@code serveXxx()} methods, it does not take a {@code Supplier} of the same type as the ResponseEntity.
   * Instead, it should be called with a method that returns a numerical ID of the new entity, which it translates to a String.
   * @param method The service method that does the work of the service, and returns an id as a Number (an Integer or Long)
   * @return A {@literal ResponseEntity<String>} holding the value of the id returned by the ServiceMethod's get() method, converted 
   * to a String.
   * @throws ResponseException if the method fails
   * @see #serve(HttpStatus, Supplier)
   * @see Supplier#get() 
   */
  public static ResponseEntity<String> serveCreatedEntity(Supplier<Number> method) throws ResponseException {
    assert method != null;
    return serve(HttpStatus.CREATED, () -> String.valueOf(method.get()));
  }

  /**
   * Serve the data, using HttpStatus.OK as the response if successful. This method delegates the work to serve().
   *
   * @param <T>    The return type
   * @param method The service method that does the work of the service, and returns an instance of type T
   * @return A {@literal ResponseEntity<T>} holding the value returned by the ServiceMethod's doService() method.
   * @throws ResponseException if the method fails
   * @see #serve(HttpStatus, Supplier)
   * @see Supplier#get()
   */
  public static <T> ResponseEntity<T> serveOK(Supplier<T> method) throws ResponseException {
    return serve(HttpStatus.OK, method);
  }

  /**
   * <p>Serve the data, specifying the HttpStatus to be used if successful, and a ServiceMethod to execute, which 
   * will usually be written as a lambda expression by the calling method. This will call the ServiceMethod's
   * doService() method inside a try/catch block. If doService() completes successfully, this method will return
   * the result packed inside a ResponseEntity object, using the specified successStatus. If doService throws an 
   * Exception, this method will return a ResponseEntity with the proper error HttpStatus and error message.
   * </p><p>
   * Since the doService() method is declared to return a ResponseException, the provided lambda expression need only
   * throw a ResponseException on failure. The error handling portion of this method will use the HttpStatus specified
   * in the ResponseException to generate the ResponseEntity.
   * </p><p>
   * This allows the developer to implement functional part of the service method inside a lambda expression without 
   * bothering with the boilerplate code used to package the successful response or handle any error.
   * </p><p>
   * <strong>Example:</strong><br>
   * </p>
   * <pre>
   *   {@literal @Override}
   *   {@literal @RequestMapping}(value = "/menuItem/{id}", produces = {"application/json"}, method = RequestMethod.GET)
   *   public ResponseEntity{@literal <MenuItemDto>} getMenuItem(@PathVariable("id") final Integer id) {
   *     return serve(HttpStatus.OK, nativeWebRequest, () -> {
   *       MenuItem menuItem = menuItemRepository.findOne(id);
   *       confirmFound(menuItem, id); // throws NotFound404Exception if null
   *       MenuItemDto dto = objectMapper.convertValue(menuItem, MenuItemDto.class);
   *       return dto;
   *     });
   *   }
   * </pre>
   * @param <T> The return type. 
   * @param successStatus The status to use if the ServiceMethod's doService() method (the lambda expression) completes
   *                      successfully.
   * @param method The service method that does the work of the service, and returns an instance of type T
   * @return A {@literal ResponseEntity<T>} holding the value returned by the ServiceMethod's doService() method.
   * @throws ResponseException if the method fails
   * @see Supplier#get() 
   * @see ResponseException
   */
  public static <T> ResponseEntity<T> serve(HttpStatus successStatus, Supplier<T> method) throws ResponseException {
    assert method != null;
    try {
      return new ResponseEntity<>(method.get(), successStatus);
    } catch (ResponseException e) {
      if (log.isDebugEnabled()) {
        log.debug(e.getMessage(), e);
      } else if (log.isWarnEnabled()) {
        log.warn(e.getMessage());
      }
      throw e;
      // I used to have another catch clause here, where I caught RuntimeExceptions and Errors, so I could log them. However, they
      // already get logged by Spring, so I don't have to bother. I still have an uncaughtExceptionHandler installed, but that's
      // mainly for exceptions thrown by other threads.
    }
  }
  
  @SuppressWarnings("unused")
  public static void logHeaders(HttpServletRequest request, String label) {
    log.debug("logHeaders from {}", label);
    logHeaders(() -> asIterator(request.getHeaderNames()), request::getHeader);
  }

  @SuppressWarnings("unused")
  public static void logHeaders(NativeWebRequest request, String label) {
    log.debug("logHeaders from {}", label);
    logHeaders(request::getHeaderNames, request::getHeader);
  }
  
  public static void logHeaders(Iterable<String> getNames, Function<String, String> getName) {
    if (log.isDebugEnabled()) {
      Iterator<String> headerNames = getNames.iterator();
      if (log.isDebugEnabled()) {
        log.debug("{} headers", countTokens(headerNames));
      }
      headerNames = getNames.iterator();
      while (headerNames.hasNext()) {
        String hName = headerNames.next();
        log.debug("{}: {}", hName, getName.apply(hName));
      }
    }
  }
  
  private static <E> Iterator<E> asIterator(Enumeration<E> e) {
    return new Iterator<E>() {
      @Override public boolean hasNext() { return e.hasMoreElements();}
      @Override public E next() { return e.nextElement();}
    };
  }

  private static int countTokens(Iterator<?> enumeration) {
    int count = 0;
    while (enumeration.hasNext()) {
      enumeration.next();
      count++;
    }
    return count;
  }
}
