package org.openapitools.framework;

import org.junit.Test;
import org.openapitools.entity.MenuItem;

import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/9/21
 * <p>Time: 12:34 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class PojoUtilityTest {
  @Test(expected = AssertionError.class)
  public void neverNullAssertionTest() {
    // NOTE: This test assumes assertions are turned on during testing. If assertions are off, this test will fail.
    PojoUtility.confirmNeverNull(new MenuItem());
    testIfAssertionsAreOn(); // makes test pass if assertions are off.
  }

  @Test(expected = AssertionError.class)
  // NOTE: This test assumes assertions are turned on during testing. If assertions are off, this test will fail.
  public void confirmFoundAssertionTest() {
    PojoUtility.confirmEntityFound("x", 0);
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
}