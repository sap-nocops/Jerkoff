/**
 * 
 */
package it.fratta.jerkoff.converter;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class ObjectDeserializer implements JsonDeserializer<Object> {

	private Gson gson;

	public ObjectDeserializer() {
		this.gson = new Gson();
	}
	
	/**
	 * 
	 * @param gson
	 */
	public ObjectDeserializer(Gson gson) {
		this.gson = gson;
	}

	public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (json.isJsonObject()) {
			JsonObject obj = json.getAsJsonObject();
			try {
				typeOfT = Class.forName(obj.get("class").getAsString());
			} catch (ClassNotFoundException e) {
				throw new JsonParseException(e);
			}
			JsonElement prim = obj.get("primitive");
			if (null != prim && prim.getAsBoolean()) {
				json = obj.get("value");
			}
		} /*
			 * else TODO errore durante il cast da Object[] to T[] if
			 * (json.isJsonArray()) { JsonParser parser = new JsonParser();
			 * JsonArray array =json.getAsJsonArray(); Object[] arr = new
			 * Object[array.size()]; for(int i = 0; i < array.size(); i++) {
			 * arr[i] = context.deserialize(array.get(i),
			 * array.get(i).getClass()); } return arr; }
			 */
		return gson.fromJson(json, typeOfT);
	}
}