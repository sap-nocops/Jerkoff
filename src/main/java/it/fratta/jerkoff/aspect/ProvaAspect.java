/**
 * 
 */
package it.fratta.jerkoff.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author luca
 *
 */
@Aspect
public class ProvaAspect {

	private static final Logger LOG = Logger.getLogger(ProvaAspect.class);

	/**
	 * 
	 * @param pjp
	 * @throws Throwable
	 */
	@Around(value = "execution ( public * *(..))")
	public Object log(ProceedingJoinPoint pjp) throws Throwable {
		LOG.info("-------------------------");
		LOG.info("ProvaAspect.log() called on " + pjp);
		LOG.info("Signature " + pjp.getSignature());
		LOG.info("Args " + arrayToString(pjp.getArgs()));
		LOG.info("Target " + pjp.getTarget());
		LOG.info("This " + pjp.getThis());
		LOG.info("-------------------------");
		try {
			Object returnValue = pjp.proceed();
			LOG.info("OK - " + returnValue);
			LOG.info("-------------------------");
			return returnValue;
		} catch (Throwable e) {
			LOG.info("-------------------------");
			LOG.error("K0 - ", e);
			throw e;
		}
	}
	
	private String arrayToString(Object[] args) {
		String res = "";
		for (Object arg : args) {
			res += arg.toString() + " ";
		}
		return res;
	}

}
