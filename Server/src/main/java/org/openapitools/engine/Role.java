package org.openapitools.engine;

import java.util.Locale;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/12/21
 * <p>Time: 3:14 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public enum Role {
  CUSTOMER("CUSTOMER"),

  ADMIN("ADMIN");

  private final String value;

  Role(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  /**
   * Get the role for a text string. Case insensitive.
   * @param value The String value
   * @return the matching role
   * @throws IllegalArgumentException if the String doesn't match.
   */
  @JsonCreator
  public static Role fromValue(String value) {
    value = value.toUpperCase(Locale.ROOT);
    for (Role b : Role.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException(String.format("Unexpected value '%s'", value));
  }
}


