/**
 * 
 */
package it.fratta.jerkoff.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
@Aspect
public class LearningAspect {

    private static final Logger LOG = Logger.getLogger(LearningAspect.class);
    
    private Properties prop;

    /**
     * 
     */
    public LearningAspect() throws IOException {
        InputStream propertiesIs = this.getClass().getClassLoader().getResourceAsStream("META-INF/learning.properties");
        prop = new Properties();
        prop.load(propertiesIs);
    }

    /**
     * 
     * @param pjp
     * @throws Throwable
     */
    @Around(value = "execution ( public * *(..))")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        LOG.info(prop.getProperty("appName"));
        LOG.info("-------------------------");
        LOG.info("ProvaAspect.log() called on " + pjp);
        LOG.info("Signature " + pjp.getSignature().toLongString());
        LOG.info("Args " + arrayToString(pjp.getArgs()));
        LOG.info("Target " + pjp.getTarget());
        LOG.info("This " + pjp.getThis());
        LOG.info("-------------------------");
        /*
         * TODO:
         * insert on MongoDB collection appName:
         * pjp.getSignature().toLongString()
         * pjp.getArgs() -> json
         * pjp.getTarget() -> json
         * pjp.getThis() -> json
         * returnValue -> json
         * e.getClass()
         */
        try {
            Object returnValue = pjp.proceed();
            LOG.info("OK - " + System.lineSeparator() + returnValue);
            LOG.info("-------------------------");
            return returnValue;
        } catch (Throwable e) {
            LOG.info("-------------------------");
            LOG.error("K0 - " + System.lineSeparator(), e);
            throw e;
        }
    }

    private String arrayToString(Object[] args) {
        String res = "";
        for (Object arg : args) {
            res += arg + " ";
        }
        return res;
    }
}
