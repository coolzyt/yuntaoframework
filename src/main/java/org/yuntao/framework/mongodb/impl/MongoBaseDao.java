package org.yuntao.framework.mongodb.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.yuntao.framework.mongodb.IMongoBaseDao;
import org.yuntao.framework.mongodb.MongoDataStore;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
/**
 * <p>Title: 具体mongoDB dao的实现继承此类</p> 
 * <p>Description: 的</p>
 * @version 1.00 
 * @since 2011-3-27
 * @author zhaoyuntao
 * 
 */
public abstract class MongoBaseDao<E> implements IMongoBaseDao<E>{
	private MongoDataStore mongoDataStore;
	protected abstract Class<E> getEntityClass();
	
	@PostConstruct
	public void init(){
		Morphia morphia = mongoDataStore.getMorphia();
		morphia.map(this.getEntityClass());
		Datastore ds = mongoDataStore.getDatastore();
		ds.ensureIndexes(this.getEntityClass());
		ds.ensureCaps();
	}
	
	@Override
	public E create(E e){
		return this.load(this.getDataStore().save(e).getId());
	}
	
	@Override
	public List<E> queryAll(String sortBy){
		return this.queryByCondition(null, sortBy);
	}
	@Override
	public List<E> queryAll(int offset,int size,String sortBy){
		Query query = createQueryByCondition(null);
		query.offset(offset);
		query.limit(size);
		if(sortBy!=null){
			query.order(sortBy);
		}
		return query.asList();
	}
	
	@Override
	public List<E> queryByCondition(Map condition,String sortBy) {
		Query query = createQueryByCondition(condition);
		if(sortBy!=null){
			query.order(sortBy);
		}
		return query.asList();
	}

	@Override
	public List<E> queryByCondition(Map condition,String sortBy, int offset, int limit) {
		Query query = createQueryByCondition(condition);
		if(sortBy!=null)
			query.order(sortBy);
		query.offset(offset);
		query.limit(limit);
		return query.asList();
	}

	
	@Override
	public E queryUniqueByCondition(Map condition) {
		Query query = createQueryByCondition(condition);
		return (E)query.get();
	}
	@Override
	public E queryUniqueByConditionNoEx(Map condition) {
		try{
			return this.queryUniqueByCondition(condition);
		}catch(Exception e){
			return null;
		}
	}
	@Override
	public boolean existByCondition(Map condition) {
		return this.countByCondition(condition)>0;
	}

	@Override
	public long countByCondition(Map condition) {
		Query query = createQueryByCondition(condition);
		return query.countAll();
	}

	@Override
	public long countAll() {
		return this.getDataStore().getCount(this.getEntityClass());
	}

	@Override
	public E load(Object id) {
		if(id instanceof String){
			id = ObjectId.massageToObjectId(id);
		}
		return this.getDataStore().get(this.getEntityClass(), id);
	}

	@Override
	public E loadNoEx(Object id) {
		try{
			if(id instanceof String){
				id = ObjectId.massageToObjectId(id);
			}
			return this.load(id);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public E loadByUniqueProperty(String property, Object value) {
		return this.getDataStore().createQuery(this.getEntityClass()).filter(property, value).get();
	}

	@Override
	public E loadByUniquePropertyNoEx(String property, Object value) {
		try{
			return this.loadByUniqueProperty(property, value);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public List<E> queryByProperty(String property, Object value,String sortBy) {
		Query q = this.getDataStore().createQuery(this.getEntityClass()).filter(property, value);
		if(sortBy != null){
			q.order(sortBy);
		}
		return q.asList();
	}

	@Override
	public long countByProperty(String property, Object value) {
		return this.getDataStore().createQuery(this.getEntityClass()).filter(property, value).countAll();
	}

	@Override
	public boolean existByProperty(String property, Object value) {
		return this.countByProperty(property, value)>0;
	}

	@Override
	public boolean existById(Object id) {
		return this.loadNoEx(id)!=null;
	}

	@Override
	public void updateByCondition(Map updateParam, Map condition) {
		this.getDataStore().update(this.createQueryByCondition(condition), this.createUpdateOperationByUpdateParam(updateParam) );
	}

	@Override
	public void updateByProperty(Map updateParam, String property, Object value) {
		Map map = new HashMap();
		map.put(property, value);
		this.updateByCondition(updateParam, map);
	}
	
	@Override
	public void removeById(Object id) {
		this.getDataStore().delete(this.getEntityClass(), id);
	}

	@Override
	public void removeByProperty(String property, Object value) {
		this.getDataStore().findAndDelete(this.getDataStore().createQuery(this.getEntityClass()).filter(property, value));
	}

	@Override
	public void removeByCondition(Map condition) {
		this.getDataStore().findAndDelete(this.createQueryByCondition(condition));
	}
	
	@Override
	public List<E> queryAll(){
		return this.queryAll(null);
	}
	
	@Override
	public List<E> queryAll(int offset,int size){
		return this.queryAll(offset,size,null);
	}
	
	@Override
	public List<E> queryByCondition(Map condition){
		return this.queryByCondition(condition,null);
	}
	
	@Override
	public List<E> queryByCondition(Map condition,int offset,int size){
		return this.queryByCondition(condition,null,offset,size);
	}
	
	@Override
	public List<E> queryByProperty(String property,Object value){
		return queryByProperty(property,value,null);
	}
	
	@Override
	public void removeAll() {
		this.getDataStore().delete(this.getEntityClass());
	}
	
	private Query createQueryByCondition(Map<String,Object> condition){
		Query query = getDataStore().createQuery(this.getEntityClass());
		if(condition!=null){
			for(String key:condition.keySet()){
				query.filter(key, condition.get(key));
			}
		}
		return query;
	}
	
	private UpdateOperations<E> createUpdateOperationByUpdateParam(Map<String,Object> updateParam){
		UpdateOperations<E> updateOperations = this.getDataStore().createUpdateOperations(getEntityClass());
		for(String key:updateParam.keySet()){
			updateOperations.set(key, updateParam.get(key));
		}
		return updateOperations;
	}
	
	
	
	@Autowired
	public void setMongoDataStore(MongoDataStore mongoDataStore) {
		this.mongoDataStore = mongoDataStore;
	}
	
	public Datastore getDataStore() {
		return mongoDataStore.getDatastore();
	}
	
}
