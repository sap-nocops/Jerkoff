package it.fratta.jerkoff.undertest;

import it.fratta.jerkoff.test.GenericTest;
import org.junit.Test;

/**
 * @author 
 */
public class ClassUnderTestTest extends GenericTest {
  /**
   *
   */
  @Test
  public void print1Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    classUnderTest.print();
    org.junit.Assert.assertTrue(true);
  }

  /**
   *
   */
  @Test
  public void print2Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    java.lang.Integer arg0 = 1;
    classUnderTest.print(arg0);
    org.junit.Assert.assertTrue(true);
  }

  /**
   *
   */
  @Test
  public void staticPrint3Test() {
    java.lang.Integer arg0 = 1;
    it.fratta.jerkoff.undertest.ClassUnderTest.staticPrint(arg0);
    org.junit.Assert.assertTrue(true);
  }

  /**
   *
   */
  @Test
  public void getCount4Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    java.lang.Integer result = classUnderTest.getCount();
    org.junit.Assert.assertTrue(result.equals(0));
  }
}
