/**
 * 
 */
package it.fratta.jerkoff.converter;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class ConverterUtils {
	
	public static final Logger LOG = Logger.getLogger(ConverterUtils.class);

	private Gson gson;

	/**
	 * 
	 */
	public ConverterUtils() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		// register custom adapter from configuration
		gsonBuilder.registerTypeHierarchyAdapter(Object.class, new ObjectSerializer(gsonBuilder.create()));
		gsonBuilder.registerTypeHierarchyAdapter(Object.class, new ObjectDeserializer(gsonBuilder.create()));
		gson = gsonBuilder.create();
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public String objectToJsonString(Object obj) {
		String json = gson.toJson(obj);
		LOG.debug(json);
		return json;
	}
	
	/**
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public Object jsonStringToObject(String json, Class<?> clazz) {
		 return gson.fromJson(json, clazz);
	}

}
