/**
 * 
 */
package it.fratta.jerkoff.converter;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public interface Converter {

	/**
	 * @param json
	 * @param clazz
	 * @return
	 */
	Object jsonStringToObject(String json, Class<?> clazz);

	/**
	 * @param obj
	 * @return
	 */
	String objectToJsonString(Object obj);

}
