/**
 * 
 */
package it.fratta.jerkoff.undertest;

import org.apache.log4j.Logger;

/**
 * @author luca
 *
 */
public class ClassUnderTest {
	
	private static final Logger LOG = Logger.getLogger(ClassUnderTest.class);
	
	private Integer count;

	public ClassUnderTest(Integer count) {
		super();
		this.count = count;
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
	
	public int getCountPowX(int x) {
		return (int) Math.pow(getCount(), x);
	}
}
