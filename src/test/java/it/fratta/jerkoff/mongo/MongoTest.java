/**
 * 
 */
package it.fratta.jerkoff.mongo;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import it.fratta.jerkoff.ClassUnderTest;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.ElementOrder.Type;
import com.google.gson.Gson;

/**
 * @author luca
 *
 */
public class MongoTest {

	private static final Logger LOG = Logger.getLogger(MongoTest.class);

	@Test
	@Ignore
	public void testInsert() {
		try (MongoClient mongoClient = new MongoClient("localhost", 27017)) {
			MongoDatabase db = mongoClient.getDatabase("prova");
			MongoCollection<Document> coll = db.getCollection("prova");
			Map<String, Object> map = new HashMap<String, Object>();
			ClassUnderTest p = new ClassUnderTest(10);
			map.put("this", convert(p));
			map.put("thisClass", p.getClass().getName());
			map.put("signature", "void test()");
			map.put("param", convert(new Object[0]));
			map.put("returnValue", 1);
			map.put("exception", convert(new IllegalAccessError("prova")));
			Document doc = new Document(map);
			coll.insertOne(doc);
			LOG.info(doc);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	private String convert(Object value) throws Exception {
		String res = null;
		try {
			LOG.debug("convert: " + value);
//			ObjectMapper mapper = new ObjectMapper();
//			mapper.setSerializationInclusion(Include.NON_NULL);
//			res = mapper.writeValueAsString(value);
			Gson gson = new Gson();
			res = gson.toJson(value);
			LOG.debug("res: " + res);
		} catch (Exception e) {
			LOG.error("Errore in write convertor", e);
			throw e;
		}
		return res;
	}

	@Test
	@Ignore
	public void testRead() {
		try (MongoClient mongoClient = new MongoClient("localhost", 27017)) {
			MongoDatabase db = mongoClient.getDatabase("prova");
			MongoCollection<Document> coll = db.getCollection("prova");
			FindIterable<Document> res = coll.find();
			for (Document doc : res) {
				String thisObj = doc.getString("this");
				LOG.info(thisObj);
				String thisClass = doc.getString("thisClass");
				LOG.info(thisClass);
				Gson gson = new Gson();
				Class<?> cl = Class.forName(thisClass);
				Object obj = gson.fromJson(thisObj , cl);
				LOG.info(obj);
			}
		} catch (Exception e) {
			LOG.error(e);
		}
	}

}
