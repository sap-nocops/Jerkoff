package it.fratta.jerkoff.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBUtils {
	private static final Logger LOG = Logger.getLogger(MongoDBUtils.class);
	private String databaseName;
	private String host;
	private int port;
	
	public MongoDBUtils(){
		try {
			Properties prop = Utils.loadProperties("META-INF/learning.properties");
			databaseName = prop.getProperty("mongo.database");
			host = prop.getProperty("mongo.hostname");
			port = Integer.parseInt(prop.getProperty("mongo.port"));
		} catch (IOException e) {
			databaseName = Constants.DEFAULT_DATABASE_NAME;
			host = Constants.DEFAULT_DB_HOST;
			port = Constants.DEFAULT_DB_PORT;
		}
	}

	public void insert(String collection, Gson toBeInserted) {
		//TODO Probably connection must be handled better
		try (MongoClient mongoClient = new MongoClient(host, port)) {
			LOG.info("Inserting document");
			MongoDatabase db = mongoClient.getDatabase(databaseName);
			MongoCollection<Document> coll = db.getCollection(collection);
			Map<String, Object> map = mapGsonToMap(toBeInserted);
			Document doc = new Document(map);
			LOG.debug(doc);
			coll.insertOne(doc);
			LOG.info("Document inserted");
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	private Map<String, Object> mapGsonToMap(Gson toBeInserted) {
		//TODO
		Map<String, Object> map = new HashMap<String, Object>();
		return map;
	}
}
