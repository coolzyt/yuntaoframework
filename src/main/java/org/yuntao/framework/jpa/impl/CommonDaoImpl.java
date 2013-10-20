package org.yuntao.framework.jpa.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.yuntao.framework.jpa.CommonDao;

/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-17
 * @author zhaoyuntao
 * 
 */
@Repository
@Transactional
public class CommonDaoImpl<E> implements CommonDao {
	private EntityManager em;
    /**
     * @param em
     */
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#count(java.lang.String)
	 */
	public long count(String jpql) {
		return ((Number)em.createQuery(jpql).getSingleResult()).longValue();
	}
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#executeUpdate(java.lang.String)
	 */
	public void executeUpdate(String jpql) {
		em.createQuery(jpql).executeUpdate();
	}
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#nativeCount(java.lang.String)
	 */
	public long nativeCount(String sql) {
		return ((Number)em.createNativeQuery(sql).getSingleResult()).longValue();
	}
	
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#nativeExecuteUpdate(java.lang.String)
	 */
	public void nativeExecuteUpdate(String sql) {
		em.createNativeQuery(sql).executeUpdate();
	}
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#nativeQuery(java.lang.String)
	 */
	public List nativeQuery(String sql) {
		return em.createNativeQuery(sql).getResultList();
	}
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#nativeQuery(java.lang.String, java.lang.Class)
	 */
	public <T> List<T> nativeQuery(String sql, Class<T> entityClass) {
		return em.createNativeQuery(sql,entityClass).getResultList();
	}
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#nativeQuery(java.lang.String, int, int)
	 */
	public List nativeQuery(String sql, int offset, int limit) {
		Query query = em.createNativeQuery(sql);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.getResultList();
	}
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#nativeQuery(java.lang.String, int, int, java.lang.Class)
	 */
	public <T> List<T> nativeQuery(String sql, int offset, int limit,
			Class<T> entityClass) {
		Query query = em.createNativeQuery(sql,entityClass);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.getResultList();
	}
	
	
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#nativeQuery(java.lang.String, int, int, java.lang.Class)
	 */
	public <T> List<T> nativeQuery(String sql, Object[] params,
			Class<T> entityClass) {
		Query query = em.createNativeQuery(sql,entityClass);
		setQueryParameters(query,params);
		return query.getResultList();
	}
	
	@Override
	public long nativeCount(String sql, Object[] params) {
		Query query = em.createNativeQuery(sql);
		setQueryParameters(query,params);
		return ((Number)query.getSingleResult()).longValue();
	}
	
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#nativeQueryUniqueResult(java.lang.String)
	 */
	public Object nativeQueryUniqueResult(String sql) {
		return em.createNativeQuery(sql).getSingleResult();
	}
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#query(java.lang.String)
	 */
	public List query(String jpql) {
		return em.createQuery(jpql).getResultList();
	}
	
	public List query(String jpql, Object[] param) {
		Query query = constructConditionQuery(jpql,param);
		return query.getResultList();
	}
	
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#query(java.lang.String, int, int)
	 */
	public List query(String jpql, int offset, int limit) {
		Query query =  em.createQuery(jpql);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.getResultList();
	}
	/* (non-Javadoc)
	 * @see com.surfilter.framework.dao.ICommonDao#queryUniqueResult(java.lang.String)
	 */
	public Object queryUniqueResult(String jpql) {
		Query query =  em.createQuery(jpql);
		return query.getSingleResult();
	}
	
	public List nativeQuery(String sql, Object[] param) {
		Query query = constructConditionNativeQuery(sql,param);
		return query.getResultList();
	}

	private Query constructConditionNativeQuery(String sql,Object[] params){
		Query query = em.createNativeQuery(sql);
		setQueryParameters(query,params);
		return query;
	}
	
	private Query constructConditionQuery(String sql,Object[] params){
		Query query = em.createQuery(sql);
		setQueryParameters(query,params);
		return query;
	}
	
	private void setQueryParameters(Query query,Object[] params){
		for(int i=0;i<params.length;i++){
			Class type = params[i].getClass();
			if(type == java.sql.Timestamp.class)
				query.setParameter(i, (Timestamp)params[i],TemporalType.TIMESTAMP);
			else if (type == java.sql.Date.class || type == java.util.Date.class)
				query.setParameter(i, (java.sql.Date)params[i],TemporalType.DATE);
			else 
				query.setParameter(i, params[i]);
		}
	}
	
	public <T> List<T> nativeQuery(String sql, Object[] params, int offset,
			int limit, Class<T> entityClass) {
		Query query = em.createNativeQuery(sql,entityClass);
		setQueryParameters(query,params);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}
	
}
