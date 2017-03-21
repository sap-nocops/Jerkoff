package it.fratta.jerkoff.mongo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import it.fratta.jerkoff.mongo.MongoDBDao;
import it.fratta.jerkoff.util.LogUtils;
import it.fratta.jerkoff.util.PropertiesUtils;

/**
 * 
 * @author sap-nocops
 *
 */
public class MongoDBDaoImpl implements MongoDBDao {
	private static Logger LOG;
	private String databaseName;
	private String host;
	private int port;
	private MongoClient mongoClient;

	/**
	 * @param prop
	 * 
	 */
	public MongoDBDaoImpl(Properties prop) {
	    LOG = LogUtils.getLogger(prop, MongoDBDaoImpl.class);
		databaseName = PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.MONGO_DB);
		host = PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.MONGO_HOST);
		port = PropertiesUtils.getRequiredIntProperty(prop, PropertiesUtils.MONGO_PORT);
		mongoClient = new MongoClient(host, port);
	}

	@Override
	public void insert(String collection, Map<String, Object> map) {
		try {
			LOG.info("Inserting document");
			MongoDatabase db = mongoClient.getDatabase(databaseName);
			MongoCollection<Document> coll = db.getCollection(collection);
			Document doc = new Document(map);
			LOG.debug(doc);
			coll.insertOne(doc);
			LOG.info("Document inserted");
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	@Override
	public List<Document> find(String collection, String signature) {
		List<Document> res = new ArrayList<Document>();
		try {
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
	
	@PreDestroy
	private void close() {
		mongoClient.close();
	}

}
