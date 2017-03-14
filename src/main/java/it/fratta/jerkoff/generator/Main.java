/**
 * 
 */
package it.fratta.jerkoff.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.common.reflect.ClassPath;
import com.squareup.javapoet.TypeSpec;

import it.fratta.jerkoff.util.Constants;
import it.fratta.jerkoff.util.PojoCreatorUtils;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class Main implements Constants {
    public static final Logger LOG = Logger.getLogger(Main.class);
    
    /**
     * Deve diventare un maven plugin
     * 
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        LOG.info("start main");
        InputStream propertiesIs = Main.class.getClassLoader().getResourceAsStream("META-INF/generator.properties");
        Properties prop = new Properties();
        prop.load(propertiesIs);
        LOG.info("Scan package: " + prop.getProperty("gen.packageToScan"));
        ClassPath classpath = ClassPath.from(Main.class.getClassLoader()); 
        File sourcePath = new File( prop.getProperty("gen.targetFolder"));
        for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClassesRecursive(prop.getProperty("gen.packageToScan"))) {
            Class<?> clazz = classInfo.load();
            LOG.info("Creating tests for public methods of " + clazz.getSimpleName());
            TypeSpec classTest = PojoCreatorUtils.getTypeSpec(clazz, prop);
            PojoCreatorUtils.writeJavaFile(sourcePath, clazz, classTest);
        }
        LOG.info("end main");
    }
}
