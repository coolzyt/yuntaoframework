package org.yuntao.framework.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-17
 * @author zhaoyuntao
 * 
 */
public interface BaseDao<E> {
	EntityManager getEntityManager();
	List<E> queryByCondition(Map condition,String sortBy);
	List<E> queryAll(String sortBy);
	List<E> queryAll(int offset,int size,String sortBy);
	List<E> queryByCondition(Map condition,String sortBy,int offset,int size);
	E queryUniqueByCondition(Map condition);
	E queryUniqueByConditionNoEx(Map condition);
	boolean existByCondition(Map condition);
	long countByCondition(Map condition);
	long countAll();
	E load(Object id);
	E loadNoEx(Object id);
	E loadByUniqueProperty(String property,Object value);
	E loadByUniquePropertyNoEx(String property,Object value);
	List<E> queryByProperty(String property,Object value,String sortBy);
	void updateByCondition(Map updateParam,Map condition);
	void updateByProperty(Map updateParam,String property,Object value);
	long countByProperty(String property,Object value);
	boolean existByProperty(String property,Object value);
	boolean existById(Object id);
	E create(E e);
	void removeById(Object id);
	void removeByProperty(String property,Object value);
	void removeByCondition(Map condition);
	void removeAll();
	List<E> queryAll();
	List<E> queryAll(int offset,int size);
	List<E> queryByCondition(Map condition);
	List<E> queryByCondition(Map condition,int offset,int size);
	List<E> queryByProperty(String property,Object value);
}
