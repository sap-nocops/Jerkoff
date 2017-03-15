/**
 * 
 */
package it.fratta.jerkoff.aspect;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.google.common.base.Optional;

import it.fratta.jerkoff.util.MongoDBUtils;
import it.fratta.jerkoff.util.Utils;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
@Aspect
public class LearningAspect {

	private static final Logger LOG = Logger.getLogger(LearningAspect.class);

	private Properties prop;
	private MongoDBUtils mongo;

	/**
	 * 
	 */
	public LearningAspect() {
		try {
			prop = Utils.loadProperties("META-INF/learning.properties");
		} catch (IOException e) {
			LOG.error(e);
		}
		mongo = new MongoDBUtils(prop);
	}

	/**
	 * TODO perfezionare pointcut
	 * 
	 * @param pjp
	 * @throws Throwable
	 */
	@Around(value = "execution ( public * *(..)) && within(it.fratta.jerkoff.undertest.*)")
	public Object log(ProceedingJoinPoint pjp) throws Throwable {
		try {
			Object returnValue = pjp.proceed();
			mongo.insert(prop.getProperty("appName"), pjp, Optional.fromNullable(returnValue), Optional.absent());
			return returnValue;
		} catch (Throwable e) {
			mongo.insert(prop.getProperty("appName"), pjp, Optional.absent(), Optional.of(e));
			throw e;
		}
	}

}
