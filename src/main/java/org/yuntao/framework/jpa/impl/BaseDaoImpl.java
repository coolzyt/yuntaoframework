package org.yuntao.framework.jpa.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.yuntao.framework.jpa.BaseDao;

/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-17
 * @author zhaoyuntao
 * 
 */
@Transactional
public abstract class BaseDaoImpl<E> implements BaseDao<E> {

	private EntityManager em;
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    public EntityManager getEntityManager() {
        return em;
    }
    private String entityName;
    private String ENTITY_ALIAS = "e";
	/**
	 * @return 实体的class
	 */
	protected abstract Class<E> getEntityClass();
	
	/**
	 * @return 实体名称
	 */
	private String getEntityName(){
		if(entityName==null)
			entityName = this.getEntityClass().getSimpleName();
		return entityName;
	}
	
	/**
	 * @return 实体的别名
	 */
	private String getEntityAlias(){

		return ENTITY_ALIAS;
	}
	private Field pkField;
	private String pkName;
	/**
	 * @return 主键
	 */
	private String getPkName(){
		if(pkName != null){
			return pkName;
		}
		pkName = getPkField().getName();
		return pkName;
	}
	
	private Field getPkField(){
		if(pkField!=null){
			return pkField;
		}
		Field fields[] = this.getEntityClass().getDeclaredFields();
		for(Field f:fields){
			if(f.isAnnotationPresent(Id.class)){
				pkField = f;
			}
		}
		return pkField;
	}

	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#countAll()
	 */
	public long countAll() {
		return ((Number)em.createQuery(getDefaultCountString()).getSingleResult()).longValue();
	}

	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#load(java.lang.Long)
	 */
	public E load(Long id) {
		return em.find(this.getEntityClass(), id);
	}

	
	
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#queryAll()
	 */
	public List<E> queryAll(String sortBy) {
		StringBuilder sb = new StringBuilder();
		sb.append(getDefaultQueryString());
		if(StringUtils.isNotBlank(sortBy)){
			sb.append(" order by ").append(sortBy);
		}
		return em.createQuery(sb.toString()).getResultList();
	}

	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#removeById(java.lang.Long)
	 */
	public void removeById(Long id) {
		E e = em.find(getEntityClass(), id);
		em.remove(e);
	}


	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#create(java.lang.Object)
	 */
	public E create(E p) {
		setPkNull(p);
        em.persist(p);
		em.flush();
		em.refresh(p);
		return p;
	}
	
	private void setPkNull(Object entity){
		Field p = this.getPkField();
		if(!p.isAnnotationPresent(GeneratedValue.class)){
			return ;
		}
		Class pkType = p.getType();
		String methodName = getWriterMethodName(this.getPkName());
		try {
			Method method = entity.getClass().getDeclaredMethod(methodName, new Class[]{pkType});
			method.invoke(entity, new Object[]{null});
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#query(int, int)
	 */
	public List<E> queryAll(int offset, int limit,String sortBy) {
		StringBuilder jpql = new StringBuilder(getDefaultQueryString());
		jpql.append(" order by ").append(sortBy);
		Query query = em.createQuery(jpql.toString());
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.getResultList();
	}
	
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#loadByUniqueProperty(java.lang.String, java.lang.Object)
	 */
	public E loadByUniqueProperty(String property,Object value){
		return this.queryUniqueByCondition(getSingleConditionMap(property,value));
	}
	
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#queryByProperty(java.lang.String, java.lang.Object)
	 */
	public List<E> queryByProperty(String property,Object value,String sortBy){
		return this.queryByCondition(getSingleConditionMap(property,value),sortBy);
	}
	
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#countByProperty(java.lang.String, java.lang.Object)
	 */
	public long countByProperty(String property,Object value){
		return this.countByCondition(getSingleConditionMap(property,value));
	}

	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#existByProperty(java.lang.String, java.lang.Object)
	 */
	public boolean existByProperty(String property,Object value){
		return this.countByProperty(property, value)>0;
	}
	
	
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#existById(java.lang.Long)
	 */
	public boolean existById(Long id){
		return this.existByProperty(this.getPkName(), id);
	}
	
	public E loadByUniquePropertyNoEx(String property,Object value){
		List ret = this.queryByProperty(property, value);
		if(ret.size()!=1){
			return null;
		}
		return (E)ret.get(0);
	}
	
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#removeAll()
	 */
	public void removeAll() {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(getEntityName());
		em.createQuery(sb.toString()).executeUpdate();
	}

	/**
	 * @param id
	 * @return 读取单个实体的数据,找不到不抛异常
	 */
	public E loadNoEx(Long id){
		return loadByUniquePropertyNoEx(this.getPkName(),id);
	}
	
	@Override
	public List<E> queryByCondition(Map conditionMap, String sortBy) {
		Query query = this.createConditionQuery(conditionMap, sortBy);
		return query.getResultList();
	}
	
	@Override
	public List<E> queryByCondition(Map conditionMap, String sortBy, int offset,
			int size) {
		Query query = this.createConditionQuery(conditionMap, sortBy);
		query.setFirstResult(offset);
		query.setMaxResults(size);
		return query.getResultList();
	}
	
	@Override
	public E queryUniqueByCondition(Map conditionMap) {
		Query query = this.createConditionQuery(conditionMap, null);
		return (E)query.getSingleResult();
	}
	
	@Override
	public E queryUniqueByConditionNoEx(Map condition){
		try{
			return queryUniqueByCondition(condition);
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
		Query query = this.createConditionCount(condition, null);
		return ((Number)query.getSingleResult()).longValue();
	}
	
	@Override
	public E load(Object id) {
		return em.find(this.getEntityClass(), id);
	}
	
	@Override
	public E loadNoEx(Object id) {
		return loadByUniquePropertyNoEx(this.getPkName(),id);
	}
	
	@Override
	public void updateByCondition(Map updateParam, Map condition) {
		Query query = createConditionUpdate(updateParam,condition);
		query.executeUpdate();
	}
	
	@Override
	public void updateByProperty(Map updateParam, String property, Object value) {
		this.updateByCondition(updateParam, getSingleConditionMap(property,value));
	}
	
	@Override
	public boolean existById(Object id) {
		return this.countByProperty(this.getPkName(), id)>0;
	}
	
	@Override
	public void removeById(Object id) {
		String sql = "delete from "+this.getEntityName()+" where "+this.getPkName()+"="+id;
		Query query = em.createQuery(sql);
		query.executeUpdate();
	}
	
	@Override
	public void removeByCondition(Map condition) {
		String prefixSql = "delete from "+this.getEntityName();
		constructConditionQuery(prefixSql,condition,null).executeUpdate();
	}
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.IBaseDao#removeByProperty(java.lang.String, java.lang.Object)
	 */
	public void removeByProperty(String property,Object value) {
		this.removeByCondition(getSingleConditionMap(property,value));
	}
	@Override
	public List<E> queryAll(){
		return this.queryAll(null);
	}
	
	@Override
	public List<E> queryAll(int offset,int size){
		return this.queryAll(offset, size, null);
	}
	@Override
	public List<E> queryByCondition(Map condition){
		return this.queryByCondition(condition, null);
	}
	
	@Override
	public List<E> queryByCondition(Map condition,int offset,int size){
		return this.queryByCondition(condition,null ,offset, size);
	}
	
	@Override
	public List<E> queryByProperty(String property,Object value){
		return this.queryByProperty(property, value, null);
	}
	
	/**
	 * @param sql
	 * @param condition
	 * @return 构建条件查询的JPA Query,自动判断条件BEAN中属性是否为空
	 */
	private Query constructConditionQuery(String prefixSql,Map<String,Object> conditionMap,String sortBy){
		StringBuilder sql = new StringBuilder(prefixSql);
		sql.append(" where ");
		boolean preAdd = false;
		for(String key:conditionMap.keySet()){
			if(preAdd){
				sql.append(" and ");
			}else{
				preAdd = true;
			}
			sql.append(key);
			if(!key.matches(".*=\\s*$")){
				sql.append("=");
			}
			sql.append(":").append(key);
		}
		if(StringUtils.isNotBlank(sortBy)){
			sql.append(" order by ").append(sortBy);
		}
		Query query = em.createQuery(sql.toString());
		for(String key:conditionMap.keySet()){
			Object value = conditionMap.get(key);
			setQueryParameterByName(query,key,value);
		}
		return query;
	}
	
	private void setQueryParameterByName(Query query ,String name,Object value){
		Class type = value.getClass();
		if(type == java.sql.Timestamp.class)
			query.setParameter(name, (Timestamp)value,TemporalType.TIMESTAMP);
		else if (type == java.sql.Date.class || type == java.util.Date.class)
			query.setParameter(name, (java.util.Date)value,TemporalType.DATE);
		else 
			query.setParameter(name, value);
	}
	private void setQueryParameterByIndex(Query query ,int index,Object value){
		Class type = value.getClass();
		if(type == java.sql.Timestamp.class)
			query.setParameter(index, (Timestamp)value,TemporalType.TIMESTAMP);
		else if (type == java.sql.Date.class || type == java.util.Date.class)
			query.setParameter(index, (java.util.Date)value,TemporalType.DATE);
		else 
			query.setParameter(index, value);
	}
	
	private Query createConditionQuery(Map<String,Object> conditionMap,String sortBy){
		return this.constructConditionQuery(this.getDefaultQueryString(), conditionMap, sortBy);	
	}
	
	private Query createConditionCount(Map<String,Object> conditionMap,String sortBy){
		return this.constructConditionQuery(this.getDefaultCountString(), conditionMap, sortBy);	
	}
	
	private Query createConditionUpdate(Map<String,Object> updateMap,Map<String,Object> conditionMap){
		StringBuilder sb = new StringBuilder();
		List valueList = new ArrayList();
		sb.append("update ").append(getEntityName());
		sb.append(" set ");
		boolean addComma = false;
		int index = 0;
		for(String key : updateMap.keySet()){
			if(addComma){
				sb.append(",");
			}else{
				addComma = true;
			}
			sb.append(key).append("=").append("?").append(index++);
			valueList.add(updateMap.get(key));
		}
		sb.append(" where ");
		boolean preAdd = false;
		for(String key:conditionMap.keySet()){
			if(preAdd){
				sb.append(" and ");
			}else{
				preAdd = true;
			}
			sb.append(key);
			if(!key.matches(".*=\\s*$")){
				sb.append("=");
			}
			sb.append("?").append(index++);
			valueList.add(conditionMap.get(key));
		}
		Query query = em.createQuery(sb.toString());
		index = 0;
		for(Object value:valueList){
			setQueryParameterByIndex(query,index++,value);
		}
		return query;
	}
	
	
	/**
	 * @return 默认的查询语句 即select * from 表名
	 */
	protected String getDefaultQueryString(){
		StringBuilder jpql = new StringBuilder();
		jpql.append("select e from ")
		.append(getEntityName())
		.append(" ").append(getEntityAlias()).append(" ");
		return jpql.toString();
	}
	
	/**
	 * @return 默认的查询数量语句 即select count(e) from 表名
	 */
	protected String getDefaultCountString(){
		StringBuilder jpql = new StringBuilder();
		jpql.append("select count(").append(getEntityAlias()).append(") from ")
		.append(getEntityName())
		.append(" ").append(getEntityAlias()).append(" ");
		return jpql.toString();
	}
	
	private Map getSingleConditionMap(String property,Object value){
		Map map = new HashMap();
		map.put(property, value);
		return map;
	}
    /**
     * 根据属性名获得JavaBean的getter方法名称(getter)
     * @param property 属性名
     * @return getter方法名
     */
    public static String getReaderMethodName(String property) {
        return "get" + property.substring(0, 1).toUpperCase()
                + property.substring(1);
    }
    
    public static String getWriterMethodName(String property) {
        return "set" + property.substring(0, 1).toUpperCase()
                + property.substring(1);
    }
    
	
}
