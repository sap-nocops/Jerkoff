/**
 * 
 */
package it.fratta.jerkoff.mongo;

import java.util.List;
import java.util.Map;

import org.bson.Document;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public interface MongoDBDao {

	/**
	 * @param collection
	 * @param map
	 */
	void insert(String collection, Map<String, Object> map);

	/**
	 * @param collection
	 * @param signature
	 * @return
	 */
	List<Document> find(String collection, String signature);

}
