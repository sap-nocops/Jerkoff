/**
 * 
 */
package it.fratta.jerkoff.generator;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

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
public class MainTest {

    private static final Logger LOG = Logger.getLogger(MainTest.class);

    @Test
    @Ignore
    public void test() throws IOException {
        Properties prop = PropertiesUtils.loadProperties("META-INF/generator.properties");
        File sourcePath = new File(prop.getProperty("gen.targetFolder"));
        Class<?> clazz = Integer.class;
        MongoDBDao mongo = new MongoDBDaoImpl(prop);
        PojoCreator creator = new PojoCreatorImpl(prop, mongo);
        LOG.info("Creating tests for public methods of " + clazz.getSimpleName());
        TypeSpec classTest = creator.getTypeSpec(clazz);
        creator.writeJavaFile(sourcePath, clazz, classTest);
    }

}
