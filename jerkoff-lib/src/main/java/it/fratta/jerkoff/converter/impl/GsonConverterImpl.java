/**
 * 
 */
package it.fratta.jerkoff.converter.impl;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.fratta.jerkoff.converter.Converter;
import it.fratta.jerkoff.converter.ObjectTypeAdapterFactory;
import it.fratta.jerkoff.util.LogUtils;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class GsonConverterImpl implements Converter {
	
	public static Logger LOG;

	private Gson gson;

	/**
	 * @param prop 
	 * 
	 */
	public GsonConverterImpl(Properties prop) {
	    LOG = LogUtils.getLogger(prop, GsonConverterImpl.class);
	    GsonBuilder gsonBuilder = new GsonBuilder();
        ObjectTypeAdapterFactory del = new ObjectTypeAdapterFactory();
        gsonBuilder.registerTypeAdapterFactory(del);
        // register custom adapter from configuration
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
