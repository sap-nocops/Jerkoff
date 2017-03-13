/**
 * 
 */
package it.fratta.jerkoff.converter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import it.fratta.jerkoff.ClassUnderTest;

/**
 * @author luca
 *
 */
public class GsonTest {

	private static final Logger LOG = Logger.getLogger(GsonTest.class);

	@Test
	public void test() {
		try {
			Gson gson = new Gson();
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
			json = gson.toJson(foo); // May not serialize foo.value correctly
			LOG.info("json: " + json);
			Foo<?> fooDes = gson.fromJson(json, foo.getClass()); // Fails to
																	// deserialize
																	// foo.value
																	// as Bar
			LOG.info("fooDes: " + fooDes);
			
			
			Collection collection = new ArrayList();
		    collection.add("hello");
		    collection.add(5);
		    collection.add(new Event("GREETINGS", "guest"));
		    json = gson.toJson(collection);
		    LOG.info("json: " + json);
		    JsonParser parser = new JsonParser();
		    JsonArray array = parser.parse(json).getAsJsonArray();
		    String message = gson.fromJson(array.get(0), String.class);
		    int number = gson.fromJson(array.get(1), int.class);
		    Event event = gson.fromJson(array.get(2), Event.class);
			LOG.info("message: " + message);
			LOG.info("number: " + number);
			LOG.info("event: " + event);
			
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

}
