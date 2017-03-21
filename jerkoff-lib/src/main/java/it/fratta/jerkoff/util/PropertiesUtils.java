package it.fratta.jerkoff.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.common.base.Strings;

/**
 * 
 * @author sap-nocops
 *
 */
public class PropertiesUtils {

	public static final String APP_NAME = "appName";
	public static final String MONGO_DB = "mongo.database";
	public static final String MONGO_HOST = "mongo.hostname";
	public static final String MONGO_PORT = "mongo.port";
	public static final String PACKAGE_TO_SCAN = "gen.packageToScan";
	public static final String TARGET_FOLDER = "gen.targetFolder";
	public static final String TEST_BASE_PACKAGE = "test.basePackage";
	public static final String TEST_BASE_CLASS = "test.baseClass";
    public static final String LOG_DIR = "log4j.dir";
    public static final String LOG_PATTERN = "log4j.pattern";
    public static final String LOG_LEVEL = "log4j.level";

	private PropertiesUtils() {
		// empty constructor
	}

	/**
	 * 
	 * @param propertyFileName
	 * @return
	 * @throws IOException
	 */
	public static Properties loadProperties(String propertyFileName) throws IOException {
		InputStream propertiesIs = PropertiesUtils.class.getClassLoader().getResourceAsStream(propertyFileName);
		Properties prop = new Properties();
		prop.load(propertiesIs);
		return prop;
	}

	/**
	 * 
	 * @param prop
	 * @param key
	 * @return
	 * @throws IllegalArgumentException if the property is missing
	 */
	public static String getRequiredProperty(Properties prop, String key) {
		String p = getProperty(prop, key);
		if (Strings.isNullOrEmpty(p)) {
			throw new IllegalArgumentException("Property " + key + " is required.");
		}
		return p;
	}

	/**
	 * 
	 * @param prop
	 * @param key
	 * @return
	 */
	public static String getProperty(Properties prop, String key) {
		return prop.getProperty(key);
	}

	/**
	 * @param prop
	 * @param mongoPort
	 * @return
	 * @throws IllegalArgumentException if the property is missing or not a number
	 */
	public static int getRequiredIntProperty(Properties prop, String key) {
		try {
			return Integer.parseInt(getRequiredProperty(prop, key));
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Property " + key + " must be a number.");
		}
	}
}
