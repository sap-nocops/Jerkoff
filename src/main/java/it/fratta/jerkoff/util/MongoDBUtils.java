package it.fratta.jerkoff.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.bson.Document;

import com.google.common.base.Optional;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import it.fratta.jerkoff.converter.ConverterUtils;

/**
 * 
 * @author sap-nocops
 *
 */
public class MongoDBUtils {
	private static final Logger LOG = Logger.getLogger(MongoDBUtils.class);
	private String databaseName;
	private String host;
	private int port;
	private ConverterUtils converter;

	/**
	 * @param prop
	 * 
	 */
	public MongoDBUtils(Properties prop) {
		try {
			databaseName = prop.getProperty("mongo.database");
			host = prop.getProperty("mongo.hostname");
			port = Integer.parseInt(prop.getProperty("mongo.port"));
		} catch (Exception e) {
			databaseName = Constants.DEFAULT_DATABASE_NAME;
			host = Constants.DEFAULT_DB_HOST;
			port = Constants.DEFAULT_DB_PORT;
		}
		converter = new ConverterUtils();
	}

	/**
	 * 
	 * @param collection
	 * @param returnValue
	 * @param pjp
	 * @param ex
	 */
	public void insert(String collection, ProceedingJoinPoint pjp, Optional<Object> returnValue,
			Optional<Throwable> ex) {
		// TODO Probably connection must be handled better
		try (MongoClient mongoClient = new MongoClient(host, port)) {
			LOG.info("Inserting document");
			MongoDatabase db = mongoClient.getDatabase(databaseName);
			MongoCollection<Document> coll = db.getCollection(collection);
			Map<String, Object> map = toMap(pjp, returnValue, ex);
			Document doc = new Document(map);
			LOG.debug(doc);
			coll.insertOne(doc);
			LOG.info("Document inserted");
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	/**
	 * 
	 * @param collection
	 * @param signature
	 * @return
	 */
	public List<Document> find(String collection, String signature) {
		List<Document> res = new ArrayList<Document>();
		// TODO Probably connection must be handled better
		try (MongoClient mongoClient = new MongoClient(host, port)) {
			MongoDatabase db = mongoClient.getDatabase(databaseName);
			MongoCollection<Document> coll = db.getCollection(collection);
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("signature", signature);
			Iterable<Document> docs = coll.find(searchQuery);
			for (Document doc : docs) {
				res.add(doc);
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		return res;
	}

	/**
	 * @param pjp
	 * @param returnValue
	 * @param ex
	 * @return
	 */
	private Map<String, Object> toMap(ProceedingJoinPoint pjp, Optional<Object> returnValue, Optional<Throwable> ex) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("signature", pjp.getSignature().toLongString());
		map.put("args", converter.objectToJsonString(pjp.getArgs()));
		map.put("this", converter.objectToJsonString(pjp.getThis()));
		map.put("target", converter.objectToJsonString(pjp.getTarget()));
		map.put("returnValue", converter.objectToJsonString(returnValue.orNull()));
		map.put("exception", ex.orNull());
		return map;
	}

}
