package org.yuntao.framework.jpa;

import java.util.List;

import javax.persistence.EntityManager;
/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-17
 * @author zhaoyuntao
 *  该Dao提供通用的数据方法,目前处理不支持存储过程。
 *  对于查询：默认返回的List中的元素为Map(并且和查询获得的顺序一致)
 *  注意：所有的Map中的关键子都是小写(字段名或者字段别名)
 * 
 */
public interface CommonDao {
	EntityManager getEntityManager();
    /**
     * @param jpql
     * @return 根据完整jpql查询
     */
    List query(String jpql);
    /**
     * @param jpql
     * @return 根据完整jpql查询
     */
    List query(String jpql,Object param[]);
    /**
     * @param jpql
     * @param offset
     * @param limit
     * @return 根据完整jpql查询
     */
    List query(String jpql, int offset, int limit);
    /**
     * @param jpql
     * @return 根据完整jpql查询一条记录
     */
    Object queryUniqueResult(String jpql);
    /**
     * @param jpql
     * @return 根据完整jpql查询数量
     */
    long count(String jpql);
    /**
     * @param jpql 执行一个jpql,用于增加删除修改操作
     */
    void executeUpdate(String jpql);
    
    /**
     * @param sql
     * @return 本地查询，根据底层数据库支持的sql
     */
    List nativeQuery(String sql);
    
    /**
     * @param sql
     * @return 本地查询，根据底层数据库支持的sql
     */
    List nativeQuery(String sql,Object param[]);
    /**
     * @param <T>
     * @param sql
     * @param entityClass
     * @return 根据底层sql返回实体列表
     */
    <T> List<T> nativeQuery(String sql, Class<T> entityClass); 
    /**
     * @param sql
     * @param offset
     * @param limit
     * @return 根据底层sql返回列表（List<Object[]>）
     */
    List nativeQuery(String sql, int offset, int limit);
    /**
     * @param <T>
     * @param sql
     * @param offset
     * @param limit
     * @param entityClass
     * @return 根据底层sql返回实体列表
     */
    <T> List<T> nativeQuery(String sql, int offset, int limit, Class<T> entityClass);
    
    <T> List<T> nativeQuery(String sql, Object[] params,int offset, int limit, Class<T> entityClass);
    
    <T> List<T> nativeQuery(String sql, Object[] params, Class<T> entityClass);
    /**
     * @param sql
     * @return 根据底层sql返回一个数据
     */
    Object nativeQueryUniqueResult(String sql);
    /**
     * @param sql
     * @return 根据sql返回数量
     */
    long nativeCount(String sql);
    /**
     * @param sql
     * @return 根据sql返回数量
     */
    long nativeCount(String sql, Object[] params);
    /**
     * @param sql 执行一条sql语句，用于更新操作
     */
    void nativeExecuteUpdate(String sql);

}
