package com.neptunedreams.engine;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neptunedreams.framework.exception.BadRequest400Exception;
import com.neptunedreams.framework.exception.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * By convention, all methods that may throw a ResponseException begin with the word confirm
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/11/18
 * <p>Time: 10:26 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"UnusedReturnValue", "HardCodedStringLiteral", "OverlyBroadThrowsClause"})
public enum PojoUtility {
  ;

  private static final Logger log = LoggerFactory.getLogger(PojoUtility.class);
  private static final ObjectMapper mapper = new ObjectMapper();
	//	private static final Duration THREE_DAYS = Duration.ofDays(3L);
  public static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
  public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

  private static final Iterable<Object> emptyIterable = Collections.emptyList();

  /**
   * Returns the provided collection. If the collection is null, returns an unmodifiable empty List. This lets you
   * iterate over any collection without checking it for null: 
   * <p>
   * {@code for (String s: skipNull(maybeNullSetOfStrings)) {...}}
   * @param iterable The collection or other Iterable
   * @param <T> The type of the collection members
   * @return the supplied Iterable, or if it's null, an unmodifiable empty Iterable.
   */
  @SuppressWarnings("unchecked") // always empty, so there are no values to cast incorrectly.
  public static <T> Iterable<T> skipNull(Iterable<T> iterable) {
    if (iterable == null) {
      return (Iterable<T>) emptyIterable;
    }
    return iterable;
  }

  /**
   * Converts the String Id value to a Integer, throwing an exception if it can't.
   *
   * @param id The id as a String
   * @return the id as a Integer value
   * @throws ResponseException BAD_REQUEST (400) if id is null or is not readable as an int value.
   */
  public static Integer confirmAndDecodeInteger(final String id) throws ResponseException {
    try {
      return Integer.valueOf(id); // throws NumberFormatException on null
    } catch (NumberFormatException e) {
      throw new BadRequest400Exception(id, e);
    }
  }

  /**
   * Converts the String Id value to a Long, throwing an exception if it can't.
   *
   * @param id The id as a String
   * @return the id as a Long value
   * @throws ResponseException BAD_REQUEST (400) if id is null or is not readable as a long value.
   */
  public static Long confirmAndDecodeLong(final String id) throws ResponseException {
    try {
      return Long.valueOf(id); // throws NumberFormatException on null
    } catch (NumberFormatException e) {
      throw new BadRequest400Exception(id, e);
    }
  }

  /**
   * Use when a non-entity object should not be null. Throws a BadRequest400Exception if null. If testing for an entity, you 
   * should use confirmFound(T object, Object id), which return a NOT_FOUND (404).
   * @param object The non-entity object to test.
   * @param <T> The object type
   * @return object, only if it's not null
   * @throws ResponseException BAD_REQUEST (400) if object is null
   */
  public static <T> T confirmNeverNull(T object) throws ResponseException {
    if (object == null) {
      throw new BadRequest400Exception("Missing object");
    }
    assert !isEntityAssertion(object) : String.format("This method is not for entity objects. Use confirmFound(): %s", getEntityClass(object));
    return object;
  }

//   This used to work. Then it broke, and now it works again.
//   I'm wondering if this is a bad idea. Fortunately, it's only executed in assertions.
  private static boolean isEntityAssertion(Object object) {
    Class<?> entityClass = getEntityClass(object);
    return entityClass != null;
  }
  
  private static boolean isDeclaredEntityClass(Class<?> theClass) {
    return Arrays.stream(theClass.getDeclaredAnnotations())
        .anyMatch(a -> a.annotationType() == Entity.class);
  }
  
  private static Class<?> getEntityClass(final Object object) {
    Class<?> objectClass = object.getClass();
    Class<?> superClass = objectClass;
    
    // If the object class is an interface, this loop will end with a null superclass.
    while ((superClass != Object.class) && (superClass != null)) {
      if (isDeclaredEntityClass(superClass)) {
        return superClass;
      }
      objectClass = superClass;
      superClass = objectClass.getSuperclass();
    }
    return null;
  }
  
  @SafeVarargs
  public static <T> Set<T> asSet(T... tArray) {
    return new HashSet<>(Arrays.asList(tArray));
  }
  
  public static String getLastMessage(Throwable ex) {
    Throwable priorEx;
    String message;
    //noinspection ObjectEquality
    do {
      message = ex.getLocalizedMessage();
      priorEx = ex;
      ex = ex.getCause();
    } while ((ex != null) && (ex != priorEx));
    return message;
  }

  /**
   * Use when a value should be null. For example, if a field should not be initialized, such as the ID of an entity 
   * that is about to be created, or an end-time for an operation that has not yet ended.
   * @param object The object that should be null.
   * @throws ResponseException BAD_REQUEST (400) if the object is not null.
   */
  public static void confirmNull(Object object) throws ResponseException {
    if (object != null) {
	    //noinspection StringConcatenation
	    throw new BadRequest400Exception("non-null value: " + object);
    }
  }

  /**
   * Confirms the two objects are equal. Uses Objects.equals().
   * @param expected The expected value
   * @param actual The actual value
   * @param <T> The type of each object
   * @throws ResponseException BAD_REQUEST (400) if the objects are not equal
   * @see Objects#equals(Object, Object) 
   */
  public static <T> void confirmEqual(T expected, T actual) throws ResponseException {
    if (!Objects.equals(actual, expected)) {
      throw new BadRequest400Exception(String.format("Expected %s  Found %s", expected, actual));
    }
  }

  /**
   * Confirms the two objects are equal. Uses Objects.equals().
   * @param message The message to use if the objects are not equal
   * @param expected The expected value
   * @param actual The actual value
   * @param <T> The type of each object
   * @throws ResponseException BAD_REQUEST (400) if the objects are not equal, using the specified message
   * @see Objects#equals(Object, Object)
   */
  public static <T> void confirmEqual(String message, T expected, T actual) throws ResponseException {
    if (!Objects.equals(actual, expected)) {
      throw new BadRequest400Exception(message);
    }
  }

  /**
   * Returns the String. Throws a ResponseException if the String is null or empty. 
   * The return value is usually not used, since this is just to test for valid data.
   * @param s The String
   * @return s
   * @throws ResponseException BAD_REQUEST (400) if the String is null or empty
   */
  public static String confirmNotEmpty(String s) throws ResponseException {
    if ((s == null) || s.isEmpty()) {
      throw new BadRequest400Exception(String.format("Null or empty value: \"%s\"", s));
    }
    return s;
  }

//  // This doesn't get used, and the missing LinkedHashMap from the return statement suggests it can't work this way,
//  // so the whole thing has been removed. We probably don't need it, but I'm not entirely sure, so I'm keeping it 
//  // around until I have a clearer idea if we will need it.
//  public static <T> List<LinkedHashMap<String, ?>> convertEntities(String json) throws IOException {
//    return mapper.readValue(json, new TypeReference<List<T>>() { });
//  }

  /**
   * Convert a Collection of DTOs into a collection of the corresponding entities.
   * This convenience method isn't really very convenient, but I keep it around to remind me of how to do this kind of conversion.
   * @param inputList The list of DTOs
   * @param <I> The Input DTO type
   * @param <O> The Output entity type
   * @return A list of entities of type O
   */
  public static <I, O> List<O> convertList(Collection<I> inputList, TypeReference<List<O>> typeReference) {
    return mapper.convertValue(inputList, typeReference);
  }

//  private static Instant parse(String dateText) {
//    if (dateText == null) {
//      return null;
//    }
//    try {
//      return Instant(timeFormat.parse(dateText).getTime());
//    } catch (ParseException e) {
//      throw new IllegalStateException(String.format("Failed to parse %s", dateText), e);
//    }
//  }

  /**
   * Returns the String, or an empty String if the String is null.
   * The return value is usually not used, since this is just to test for valid data.
   * @param s The String
   * @return The original String, or an empty String if the original was empty. Never returns null.
   */
  public static String emptyIfNull(String s) {
    return (s == null) ? "" : s;
  }
  
  public static boolean isBlank(String s) {
    return (s == null) || s.isEmpty();
  }
}
