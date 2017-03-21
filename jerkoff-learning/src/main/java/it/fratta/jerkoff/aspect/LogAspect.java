/**
 * 
 */
package it.fratta.jerkoff.aspect;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

import it.fratta.jerkoff.util.LogUtils;
import it.fratta.jerkoff.util.PropertiesUtils;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class LogAspect {

    private static final Logger LOGGER;

    /**
     * 
     */
    static {
        try {
            Properties prop = PropertiesUtils.loadProperties("META-INF/learning.properties");
            LOGGER = LogUtils.getLogger(prop, LogAspect.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 
     * @param pjp
     * @return
     * @throws Throwable
     */
    public static Object log(ProceedingJoinPoint pjp) throws Throwable {
        LOGGER.info("pjp: " + pjp.getSignature().toLongString());
        try {
            Object returnValue = pjp.proceed();
            return returnValue;
        } catch (Throwable e) {
            throw e;
        }
    }

}
