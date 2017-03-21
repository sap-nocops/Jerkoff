/**
 * 
 */
package it.fratta.jerkoff.converter;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class ObjectSerializer implements JsonSerializer<Object> {

	private Gson gson;

	/**
	 * 
	 * @param gson
	 */
	public ObjectSerializer(Gson gson) {
		this.gson = gson;
	}

	public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
		JsonElement elem = null;
		if (Iterable.class.isAssignableFrom(src.getClass())) {
			JsonArray array = new JsonArray();
			for (Object obj : (Iterable<?>) src) {
				array.add(context.serialize(obj));
			}
			elem = array;
		} else if (src.getClass().isArray()) {
			JsonArray array = new JsonArray();
			for (Object obj : (Object[]) src) {
				array.add(context.serialize(obj));
			}
			elem = array;
		} else {
			elem = gson.toJsonTree(src);
			if (elem.isJsonObject()) {
				JsonObject obj = elem.getAsJsonObject();
				obj.addProperty("class", ((Class<?>)typeOfSrc).getName());
				elem = obj;
			} else {
				JsonObject obj = new JsonObject();
				obj.add("value", elem);
				obj.addProperty("class", ((Class<?>)typeOfSrc).getName());
				obj.addProperty("primitive", true);
				elem = obj;
			}
		}
		return elem;
	}
}