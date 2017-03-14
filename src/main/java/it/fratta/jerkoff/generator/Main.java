/**
 * 
 */
package it.fratta.jerkoff.generator;

import java.io.File;

import org.apache.log4j.Logger;

import com.squareup.javapoet.TypeSpec;

import it.fratta.jerkoff.undertest.ClassUnderTest;
import it.fratta.jerkoff.util.PojoCreatorUtils;

/**
 * @author luca
 *
 */
public class Main {
	private static final String SLASH = "/";
	public static final String BASE_CLASS = "GenericTest";
	public static final String BASE_PACKAGE = "it.fratta.jerkoff.test";
	private static final String PATH = "src/test/java";
	public static final Logger LOG = Logger.getLogger(Main.class);
	public static final String TEST = "Test";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOG.info("start main");
		File sourcePath = new File(PATH);
		Class<?> clazz = ClassUnderTest.class;
		TypeSpec classTest = PojoCreatorUtils.getTypeSpec(clazz);
		PojoCreatorUtils.writeJavaFile(sourcePath, clazz, classTest);
		LOG.info("end main");
	}
}
