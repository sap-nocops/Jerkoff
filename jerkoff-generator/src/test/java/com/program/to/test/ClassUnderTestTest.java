package com.program.to.test;

import it.fratta.jerkoff.test.GenericTest;
import javax.annotation.Generated;
import org.junit.Test;

/**
 * @author 
 */
@Generated(
    value = "it.fratta.jerkoff.Generator",
    date = "Fri Mar 17 00:42:48 CET 2017"
)
public class ClassUnderTestTest extends GenericTest {
  /**
   *
   */
  @Test
  public void getCountPowX1Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    int arg0 = 1;
    int result = classUnderTest.getCountPowX(arg0);
    org.junit.Assert.assertTrue(result == 0);
  }

  /**
   *
   */
  @Test
  public void staticPrint2Test() {
    java.lang.Integer arg0 = 1;
    ClassUnderTest.staticPrint(arg0);
    org.junit.Assert.assertTrue(true);
  }

  /**
   *
   */
  @Test
  public void getCount3Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    java.lang.Integer result = classUnderTest.getCount();
    org.junit.Assert.assertTrue(result.equals(0));
  }

  /**
   *
   */
  @Test
  public void incr4Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    classUnderTest.incr();
    org.junit.Assert.assertTrue(true);
  }

  /**
   *
   */
  @Test
  public void print5Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    classUnderTest.print();
    org.junit.Assert.assertTrue(true);
  }

  /**
   *
   */
  @Test
  public void print6Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    java.lang.Integer arg0 = 1;
    classUnderTest.print(arg0);
    org.junit.Assert.assertTrue(true);
  }
}