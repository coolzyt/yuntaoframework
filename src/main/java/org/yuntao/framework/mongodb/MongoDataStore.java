package org.yuntao.framework.mongodb;

import java.net.UnknownHostException;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * <p>Title: 基于spring做的mongodb morphia集成</p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-27
 * @author zhaoyuntao
 * 
 */

public class MongoDataStore {
	private String ipAddress;
	private int port = 27017;
	private String dbName = "MONGO-DEFAULT";
	private Datastore datastore;
	private Morphia morphia = new Morphia();

	public void init() throws UnknownHostException, MongoException{
		Mongo mongo = new Mongo(ipAddress,port);
		datastore = morphia.createDatastore(mongo, dbName);
	}
	
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public Datastore getDatastore() {
		return datastore;
	}
	public Morphia getMorphia() {
		return morphia;
	}
}
