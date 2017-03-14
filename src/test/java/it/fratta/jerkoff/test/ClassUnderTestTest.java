package it.fratta.jerkoff.test;

import org.junit.Before;
import org.junit.Test;

import it.fratta.jerkoff.undertest.ClassUnderTest;

/**
 * @author 
 */
public class ClassUnderTestTest extends GenericTest {
  private ClassUnderTest classUnderTest;

  /**
   *
   */
  @Before
  public void init() {
    classUnderTest = new ClassUnderTest(0);
  }

  /**
   *
   */
  @Test
  public void getCount1Test() {
    java.lang.Integer result = classUnderTest.getCount();
    org.junit.Assert.assertTrue(result.equals(0));
  }

  /**
   *
   */
  @Test
  public void print2Test() {
    classUnderTest.print();
    org.junit.Assert.assertTrue(true);
  }

  /**
   *
   */
  @Test
  public void print3Test() {
    java.lang.Integer arg0 = 1;
    classUnderTest.print(arg0);
    org.junit.Assert.assertTrue(true);
  }
}
