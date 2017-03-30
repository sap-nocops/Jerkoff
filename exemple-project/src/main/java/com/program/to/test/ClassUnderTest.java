package com.program.to.test;
/**
 * 
 */


import org.apache.log4j.Logger;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class ClassUnderTest {

    private static final Logger LOG = Logger.getLogger(ClassUnderTest.class);

    private Integer count;

    public ClassUnderTest(Integer pippo) {
        super();
        this.count = pippo;
    }

    public Integer getCount() {
        return count;
    }

    public void print() {
        LOG.info(count);
    }

    public void print(Integer count) {
        LOG.info(count);
    }
    
    public void print(Integer i, Integer y) {
        LOG.info((i + y));
    }

    public int getCountPowX(int x) {
        return (int) Math.pow(getCount(), x);
    }

    public static void staticPrint(Integer count) {
        LOG.info(count);
    }
    
    public void incr() {
        count++;
    }

}
