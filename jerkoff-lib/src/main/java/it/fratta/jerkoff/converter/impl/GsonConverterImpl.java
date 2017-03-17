/**
 * 
 */
package it.fratta.jerkoff.converter.impl;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.fratta.jerkoff.converter.Converter;
import it.fratta.jerkoff.converter.ObjectDeserializer;
import it.fratta.jerkoff.converter.ObjectSerializer;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class GsonConverterImpl implements Converter {
	
	public static final Logger LOG = Logger.getLogger(GsonConverterImpl.class);

	private Gson gson;

	/**
	 * 
	 */
	public GsonConverterImpl() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		// register custom adapter from configuration
		gsonBuilder.registerTypeHierarchyAdapter(Object.class, new ObjectSerializer(gsonBuilder.create()));
		gsonBuilder.registerTypeHierarchyAdapter(Object.class, new ObjectDeserializer(gsonBuilder.create()));
		gson = gsonBuilder.create();
	}

	@Override
	public String objectToJsonString(Object obj) {
		String json = gson.toJson(obj);
		LOG.debug(json);
		return json;
	}
	
	@Override
	public Object jsonStringToObject(String json, Class<?> clazz) {
		 return gson.fromJson(json, clazz);
	}

}
