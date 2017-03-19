/**
 * 
 */
package it.fratta.jerkoff.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class LogAspect {
    
    public static Object log(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("pjp: " + pjp.getSignature().toLongString());
        try {
            Object returnValue = pjp.proceed();
            return returnValue;
        } catch (Throwable e) {
            throw e;
        }
    }

}
