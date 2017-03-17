/**
 * 
 */
package it.fratta.jerkoff.converter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class GsonTest {

	private static final Logger LOG = Logger.getLogger(GsonTest.class);

	@Test
	public void test() {
		try {
			GsonBuilder gsonBuilder = new GsonBuilder();
			//register custom adapter from configuration
			gsonBuilder.registerTypeHierarchyAdapter(Object.class, new ObjectSerializer(gsonBuilder.create()));
			gsonBuilder.registerTypeHierarchyAdapter(Object.class, new ObjectDeserializer(gsonBuilder.create()));
			Gson gson = gsonBuilder.create();
			int oneInt = gson.fromJson("1", int.class);
			LOG.info("oneInt: " + oneInt);
			Integer oneInteger = gson.fromJson("1", Integer.class);
			LOG.info("oneInteger: " + oneInteger);
			Long oneLong = gson.fromJson("1", Long.class);
			LOG.info("oneLong: " + oneLong);
			Boolean falseBoolean = gson.fromJson("false", Boolean.class);
			LOG.info("falseBoolean: " + falseBoolean);
			String str = gson.fromJson("\"abc\"", String.class);
			LOG.info("str: " + str);
			String[] arrayStr = gson.fromJson("[\"abc\"]", String[].class);
			LOG.info("arrayStr: " + arrayStr);

			BagOfPrimitives obj = new BagOfPrimitives();
			String json = gson.toJson(obj);
			LOG.info("json: " + json);
			BagOfPrimitives obj2 = gson.fromJson(json, BagOfPrimitives.class);
			LOG.info("obj2: " + obj2);

			json = "[1,2,3,4,5]";
			Type collectionType = new TypeToken<Collection<Integer>>() {
			}.getType();
			Collection<Integer> ints2 = gson.fromJson(json, collectionType);
			LOG.info("ints2: " + ints2);

			Foo<Integer> foo = new Foo<Integer>();
			foo.value = 1;
			json = gson.toJson(foo); 
			LOG.info("json: " + json);
			Foo<?> fooDes = gson.fromJson(json, foo.getClass()); 
			LOG.info("fooDes: " + fooDes);

			Collection collection = new ArrayList();
			collection.add("hello");
			collection.add(5);
			collection.add(new Event("GREETINGS", "guest"));

			json = gson.toJson(collection.toArray());
			LOG.info("json: " + json);
			JsonParser parser = new JsonParser();
			JsonArray array = parser.parse(json).getAsJsonArray();
			String message = gson.fromJson(array.get(0), String.class);
			Integer number = gson.fromJson(array.get(1), Integer.class);
			Event event = gson.fromJson(array.get(2), Event.class);
			LOG.info("message: " + message);
			LOG.info("number: " + number);
			LOG.info("event: " + event);

			Object[] arrayObj = gson.fromJson(json, Object[].class);
			LOG.info("arrayObj: " + arrayObj);

			json = "{\"value1\":1,\"value2\":\"abc\", \"class\":\"it.fratta.jerkoff.converter.GsonTest$BagOfPrimitives\"}";
			BagOfPrimitives obj3 = gson.fromJson(json, BagOfPrimitives.class);
			LOG.info("obj3: " + obj3);
			JsonElement elem = parser.parse(json);
			JsonElement elemClass = elem.getAsJsonObject().get("class");
			LOG.info("elemClass: " + elemClass.getAsString());

			Comparable comp = new Long(1L);
			json = gson.toJson(comp);
			LOG.info("json: " + json);
			Comparable comp2 = gson.fromJson(json, Comparable.class);
			LOG.info("comp2: " + comp2);

			if (Iterable.class.isAssignableFrom(ints2.getClass())) {
				Object[] arr = (Object[]) ints2.toArray();
				LOG.info("coll " + arr);
			}

			if (arrayStr.getClass().isArray()) {
				Object[] arr = (Object[]) arrayStr;
				LOG.info("arrStr " + arr);
			}

			BagOfCollections objC = new BagOfCollections();
			objC.collection = new ArrayList();
			objC.collection.add("hello");
			objC.collection.add(comp);
			objC.collection.add(new Event("GREETINGS", "guest"));
			json = gson.toJson(objC);
			LOG.info("json: " + json);
			BagOfCollections objC2 = gson.fromJson(json, BagOfCollections.class);
			LOG.info("objC2: " + objC2);

		} catch (Exception e) {
			LOG.error(e);
		}
	}

	class BagOfPrimitives {
		private int value1 = 1;
		private String value2 = "abc";
		private transient int value3 = 3;

		BagOfPrimitives() {
			// no-args constructor
		}
	}

	class BagOfCollections {
		private int value1 = 1;
		Collection collection;

		BagOfCollections() {

		}
	}

	class Foo<T> {
		T value;
	}

	class Event {
		private String name;
		private String source;

		public Event(String name, String source) {
			this.name = name;
			this.source = source;
		}

		@Override
		public String toString() {
			return String.format("(name=%s, source=%s)", name, source);
		}
	}

	private class ObjectDeserializer implements JsonDeserializer<Object> {

		private Gson gson;


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
			} else /*
					* TODO errore durante il cast da Object[] to T[]
					 * if (json.isJsonArray()) { JsonParser parser = new
					 * JsonParser(); JsonArray array =json.getAsJsonArray();
					 * Object[] arr = new Object[array.size()]; for(int i = 0; i
					 * < array.size(); i++) { arr[i] =
					 * context.deserialize(array.get(i),
					 * array.get(i).getClass()); } return arr; }
					 */ {
				LOG.info(typeOfT);
			}
			return gson.fromJson(json, typeOfT);
		}
	}

	private class ObjectSerializer implements JsonSerializer<Object> {

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
				for (Object obj : (Iterable) src) {
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
					obj.addProperty("class", typeOfSrc.getTypeName());
					elem = obj;
				} else {
					JsonObject obj = new JsonObject();
					obj.add("value", elem);
					obj.addProperty("class", typeOfSrc.getTypeName());
					obj.addProperty("primitive", true);
					elem = obj;
				}
			}
			return elem;
		}
	}

}