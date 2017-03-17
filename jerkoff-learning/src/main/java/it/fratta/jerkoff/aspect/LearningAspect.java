/**
 * 
 */
package it.fratta.jerkoff.aspect;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.google.common.base.Optional;

import it.fratta.jerkoff.converter.Converter;
import it.fratta.jerkoff.converter.impl.GsonConverterImpl;
import it.fratta.jerkoff.mongo.MongoDBDao;
import it.fratta.jerkoff.mongo.impl.MongoDBDaoImpl;
import it.fratta.jerkoff.util.PropertiesUtils;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
@Aspect
public class LearningAspect {

	private static final Logger LOG = Logger.getLogger(LearningAspect.class);

	private MongoDBDao mongo;
	private Converter converter;
	private String appName;

	/**
	 * 
	 */
	public LearningAspect() {
		try {
			Properties prop = PropertiesUtils.loadProperties("META-INF/learning.properties");
			appName = PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.APP_NAME);
			mongo = new MongoDBDaoImpl(prop);
			converter = new GsonConverterImpl();
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	/**
	 * TODO perfezionare pointcut
	 * 
	 * @param pjp
	 * @throws Throwable
	 */
	@Around(value = "execution ( public * *(..)) && within(com.program.to.test.*)")
	public Object log(ProceedingJoinPoint pjp) throws Throwable {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			before(map, pjp);
			Object returnValue = pjp.proceed();
			after(map, pjp, Optional.fromNullable(returnValue), Optional.absent());
			mongo.insert(appName, map);
			return returnValue;
		} catch (Throwable e) {
			after(map, pjp, Optional.absent(), Optional.of(e));
			mongo.insert(appName, map);
			throw e;
		}
	}

	/**
	 * @param map
	 * @param pjp
	 */
	private void before(Map<String, Object> map, ProceedingJoinPoint pjp) {
		map.put("signature", pjp.getSignature().toLongString());
		map.put("argsBefore", converter.objectToJsonString(pjp.getArgs()));
		map.put("thisBefore", converter.objectToJsonString(pjp.getThis()));
		map.put("targetBefore", converter.objectToJsonString(pjp.getTarget()));
	}

	/**
	 * @param pjp
	 * @param returnValue
	 * @param ex
	 * @param map
	 * @return
	 */
	private void after(Map<String, Object> map, ProceedingJoinPoint pjp, Optional<Object> returnValue,
			Optional<Throwable> ex) {
		map.put("argsAfter", converter.objectToJsonString(pjp.getArgs()));
		map.put("thisAfter", converter.objectToJsonString(pjp.getThis()));
		map.put("targetAfter", converter.objectToJsonString(pjp.getTarget()));
		map.put("returnValue", converter.objectToJsonString(returnValue.orNull()));
		map.put("exception", ex.orNull());
	}

}
