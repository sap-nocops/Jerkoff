/**
 * 
 */
package it.fratta.jerkoff.generator;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.google.common.reflect.ClassPath;
import com.squareup.javapoet.TypeSpec;

import it.fratta.jerkoff.mongo.MongoDBDao;
import it.fratta.jerkoff.mongo.impl.MongoDBDaoImpl;
import it.fratta.jerkoff.poet.PojoCreator;
import it.fratta.jerkoff.poet.impl.PojoCreatorImpl;
import it.fratta.jerkoff.util.PropertiesUtils;

/**
 * @author ElGansoSnowhiteDurden
 *
 */

@Mojo(name = "generate-test")
public class Main extends AbstractMojo {

    /**
     * 
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        getLog().info("start main");
        try {
            Properties prop = PropertiesUtils.loadProperties("META-INF/generator.properties");
            String packageToScan = PropertiesUtils.getRequiredProperty(prop,
                    PropertiesUtils.PACKAGE_TO_SCAN);
            getLog().info("Scan package: " + packageToScan);
            ClassPath classpath = ClassPath.from(Main.class.getClassLoader());
            File sourcePath = new File(
                    PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.TARGET_FOLDER));
            MongoDBDao mongo = new MongoDBDaoImpl(prop);
            PojoCreator creator = new PojoCreatorImpl(prop, mongo);
            for (ClassPath.ClassInfo classInfo : classpath
                    .getTopLevelClassesRecursive(packageToScan)) {
                Class<?> clazz = classInfo.load();
                getLog().info("Creating tests for class " + clazz.getSimpleName());
                TypeSpec classTest = creator.getTypeSpec(clazz);
                creator.writeJavaFile(sourcePath, clazz, classTest);
            }
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException(e.getMessage());
        }
        getLog().info("end main");
    }
}
