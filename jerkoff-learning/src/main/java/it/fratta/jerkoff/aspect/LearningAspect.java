/**
 * 
 */
package it.fratta.jerkoff.aspect;

import java.io.IOException;
import java.util.Calendar;
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
import it.fratta.jerkoff.util.LogUtils;
import it.fratta.jerkoff.util.PropertiesUtils;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
@Aspect
public class LearningAspect {

    private static final String LEARNING_PROPERTIES = "META-INF/learning.properties";

	private static Logger LOG;

    private MongoDBDao mongo;
    private Converter converter;
    private String appName;

    /**
     * 
     */
    public LearningAspect() {
        try {
            Properties prop = PropertiesUtils.loadProperties(LEARNING_PROPERTIES);
            LOG = LogUtils.getLogger(prop, LearningAspect.class);
            appName = PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.APP_NAME);
            mongo = new MongoDBDaoImpl(prop);
            converter = new GsonConverterImpl(prop);
        } catch (IOException e) {
            LOG.error(e);
        }
    }
    

    /**
     * 
     * @param pjp
     * @throws Throwable
     */
    @Around(value = "execution ( public * *(..)) ")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        LOG.info("around: " + pjp.getSignature().toLongString());
        Map<String, Object> map = new HashMap<String, Object>();
        boolean ok = true;
        try {
            ok = ok && before(map, pjp);
            Object returnValue = pjp.proceed();
            Optional<String> e = Optional.absent();
            ok = ok && after(map, pjp, Optional.fromNullable(returnValue), e);
            if (ok) {
                mongo.insert(appName, map);
            }
            return returnValue;
        } catch (Throwable e) {
            ok = ok && after(map, pjp, Optional.absent(), Optional.of(e.getClass().getName()));
            if (ok) {
                mongo.insert(appName, map);
            }
            throw e;
        }
    }

    /**
     * @param map
     * @param pjp
     */
    private boolean before(Map<String, Object> map, ProceedingJoinPoint pjp) {
        boolean ok = true;
        try {
            map.put("signature", pjp.getSignature().toLongString());
            map.put("argsBefore", converter.objectToJsonString(pjp.getArgs()));
            map.put("thisBefore", converter.objectToJsonString(pjp.getThis()));
            map.put("targetBefore", converter.objectToJsonString(pjp.getTarget()));
            map.put("dataInserimento", Calendar.getInstance().getTime());
        } catch (Exception e) {
            LOG.debug(e);
            ok = false;
        }
        return ok;
    }

    /**
     * @param pjp
     * @param returnValue
     * @param ex
     * @param map
     * @return
     */
    private boolean after(Map<String, Object> map, ProceedingJoinPoint pjp,
            Optional<Object> returnValue, Optional<String> ex) {
        boolean ok = true;
        try {
            map.put("argsAfter", converter.objectToJsonString(pjp.getArgs()));
            map.put("thisAfter", converter.objectToJsonString(pjp.getThis()));
            map.put("targetAfter", converter.objectToJsonString(pjp.getTarget()));
            map.put("returnValue", converter.objectToJsonString(returnValue.orNull()));
            map.put("exception", ex.orNull());
        } catch (Exception e) {
            LOG.debug(e);
            ok = false;
        }
        return ok;
    }

}
