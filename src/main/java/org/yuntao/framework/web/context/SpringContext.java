package org.yuntao.framework.web.context;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @version 1.00 Dec 26, 2008
 * @author zhaoyuntao
 * 
 * Modified History:
 * 
 */
public class SpringContext {
	public static final String DEFAULT_PERSISTENCE_MANAGER_FACTORY_BEAN_NAME = "entityManagerFactory";
	private static ApplicationContext applicationContext;
	private static String persistenceManagerFactoryBeanName = DEFAULT_PERSISTENCE_MANAGER_FACTORY_BEAN_NAME;


	/**
	 * @return 返回spring容器
	 */
	public static ApplicationContext getApplicationContext() {
		return getRootApplicationContext();
	}

	private static ApplicationContext getRootApplicationContext(){
		return applicationContext;
	}
	
	public static void initClassPathXmlApplicationContext(String[] configLocations){
		applicationContext = new ClassPathXmlApplicationContext(configLocations);
	}
	
	public static void initFileSystemXmlApplicationContext(String[] configLocations){
		applicationContext = new FileSystemXmlApplicationContext(configLocations);
	}
	/**
	 * @param <T>
	 * @param beanName
	 * @param clazz
	 * @return 从spring容器中取出一个bean
	 */
	public static <T> T getBean(String beanName, Class<T> clazz) {
		Object ret = getApplicationContext().getBean(
				beanName);
		if (ret == null) {
			throw new RuntimeException("Spring容器里没有名字为" + beanName + "的bean");
		}
		return clazz.cast(ret);
	}

	/**
	 * 返回spring容器管理的一个实体管理器
	 * @return 实体管理器
	 */
	public static EntityManager getEntityManager() {
		EntityManagerFactory emf = (EntityManagerFactory) getApplicationContext()
				.getBean(persistenceManagerFactoryBeanName);
		return emf.createEntityManager();
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringContext.applicationContext = applicationContext;
	}
	
	public static void setPersistenceManagerFactoryBeanName(
			String persistenceManagerFactoryBeanName) {
		SpringContext.persistenceManagerFactoryBeanName = persistenceManagerFactoryBeanName;
	}
}
