package com.program.to.test;

import it.fratta.jerkoff.test.GenericTest;
import javax.annotation.Generated;
import org.junit.Test;

/**
 * @author 
 */
@Generated(
    value = "it.fratta.jerkoff.Generator",
    date = "Tue Mar 21 02:01:52 CET 2017"
)
public class ClassUnderTestTest extends GenericTest {
  /**
   *
   */
  @Test
  public void getCount1Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    java.lang.Integer result = classUnderTest.getCount();
    org.junit.Assert.assertTrue(result.equals(0));
  }

  /**
   *
   */
  @Test
  public void getCountPowX2Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    int int1 = 1;
    int result = classUnderTest.getCountPowX(int1);
    org.junit.Assert.assertTrue(result == 0);
  }

  /**
   *
   */
  @Test
  public void staticPrint3Test() {
    java.lang.Integer integer1 = 1;
    ClassUnderTest.staticPrint(integer1);
    org.junit.Assert.assertTrue(true);
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
    java.lang.Integer integer1 = 1;
    java.lang.Integer integer2 = 1;
    classUnderTest.print(integer1,integer2);
    org.junit.Assert.assertTrue(true);
  }

  /**
   *
   */
  @Test
  public void print6Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    classUnderTest.print();
    org.junit.Assert.assertTrue(true);
  }

  /**
   *
   */
  @Test
  public void print7Test() {
    ClassUnderTest classUnderTest = new ClassUnderTest(0);
    java.lang.Integer integer1 = 1;
    classUnderTest.print(integer1);
    org.junit.Assert.assertTrue(true);
  }
}
