package it.fratta.jerkoff.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import it.fratta.jerkoff.generator.Main;

public interface Utils {
	static Properties loadProperties(String propertyFileName) throws IOException {
		InputStream propertiesIs = Main.class.getClassLoader().getResourceAsStream(propertyFileName);
		Properties prop = new Properties();
		prop.load(propertiesIs);
		return prop;
	}
}
