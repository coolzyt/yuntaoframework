package org.yuntao.framework.config;

import java.io.IOException;
import java.util.Properties;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company: </p>
 * 
 * @version 1.00
 * @since 2012-2-22
 * @author zhaoyuntao
 * 
 */
public class FrameworkConfig {
    private static Properties prop = new Properties();
    private static final String CONFIG_PATH = "/yuntao.properties"; 
    static{
        try {
            prop.load(FrameworkConfig.class.getResourceAsStream(CONFIG_PATH));
        } catch (IOException e) {
            throw new IllegalArgumentException("找不到配置文件yuntao.propreties");
        }
    }
    
    public static String getConfigValue(String configName){
        return prop.getProperty(configName);
    }
    private static String cfgNameAppClassRoot = "app.class.root";
    
    public static String getAppClassRoot(){
        return getConfigValue(cfgNameAppClassRoot);
    }
}
