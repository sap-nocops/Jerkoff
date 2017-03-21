/**
 * 
 */
package it.fratta.jerkoff.util;

import java.util.Properties;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class LogUtils {
    
    private LogUtils(){
        //empty constructor
    }
    
    /**
     * 
     * @param prop
     * @param clazz
     * @return
     */
    public static Logger getLogger(Properties prop, Class<?> clazz) {
        Logger LOG = Logger.getLogger(clazz);
        String appName = PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.APP_NAME);
        String fileDir = PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.LOG_DIR);
        String patternLayout = PropertiesUtils.getRequiredProperty(prop,
                PropertiesUtils.LOG_PATTERN);
        String level = PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.LOG_LEVEL);
        LOG.setLevel(Level.toLevel(level, Level.INFO));
        DailyRollingFileAppender rollingAppender = new DailyRollingFileAppender();
        rollingAppender.setFile(fileDir + "/" + appName + ".log");
        rollingAppender.setLayout(new PatternLayout(patternLayout));
        rollingAppender.setDatePattern("'.'yyyy-MM-dd");
        rollingAppender.activateOptions();
        LOG.addAppender(rollingAppender);
        LOG.setAdditivity(false);
        return LOG;
    }

}
