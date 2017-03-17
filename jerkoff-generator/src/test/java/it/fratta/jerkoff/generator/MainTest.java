/**
 * 
 */
package it.fratta.jerkoff.generator;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.program.to.test.ClassUnderTest;
import com.squareup.javapoet.TypeSpec;

import it.fratta.jerkoff.util.PojoCreatorUtils;
import it.fratta.jerkoff.util.PropertiesUtils;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class MainTest {

	private static final Logger LOG = Logger.getLogger(MainTest.class);

	@Test
	public void test() throws IOException {
		Properties prop = PropertiesUtils.loadProperties("META-INF/generator.properties");
		File sourcePath = new File(prop.getProperty("gen.targetFolder"));
		Class<?> clazz = ClassUnderTest.class;
		LOG.info("Creating tests for public methods of " + clazz.getSimpleName());
		TypeSpec classTest = PojoCreatorUtils.getTypeSpec(clazz, prop);
		PojoCreatorUtils.writeJavaFile(sourcePath, clazz, classTest);
	}

}
