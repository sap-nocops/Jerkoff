package it.fratta.jerkoff.undertest;

import it.fratta.jerkoff.test.GenericTest;
import org.junit.Before;
import org.junit.Test;

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
  public void print1Test() {
    classUnderTest.print();
    org.junit.Assert.assertTrue(true);
  }

  /**
   *
   */
  @Test
  public void print2Test() {
    java.lang.Integer arg0 = 1;
    classUnderTest.print(arg0);
    org.junit.Assert.assertTrue(true);
  }

  /**
   *
   */
  @Test
  public void getCountPowX3Test() {
    int arg0 = 1;
    int result = classUnderTest.getCountPowX(arg0);
    org.junit.Assert.assertTrue(result.equals(0));
  }

  /**
   *
   */
  @Test
  public void getCount4Test() {
    java.lang.Integer result = classUnderTest.getCount();
    org.junit.Assert.assertTrue(result.equals(0));
  }
}
