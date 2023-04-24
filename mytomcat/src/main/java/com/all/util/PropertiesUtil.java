package com.all.util;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    public static final String DEFAULT_CHARSET_NAME = "utf-8";

    /**
     * 从classpath根下获取Properties文件
     * @param configFile
     * @return
     */
    public static Properties getConfig(String configFile) {
        return getConfig(configFile, DEFAULT_CHARSET_NAME);
    }

    /**
     * 从classpath根下获取Properties文件
     * @param configFile
     * @param charsetName
     * @return
     */
    public static Properties getConfig(String configFile, String charsetName) {
        if (charsetName == null) {
            throw new IllegalArgumentException("Argu charsetName cannot be null");
        }
        if (configFile == null) {
            throw new IllegalArgumentException("Argu configFile cannot be null");
        }
        Properties prop =  new Properties();
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(configFile);
        if( null == in ) return null;
        try  {
            prop.load(in);
        }  catch  (IOException e) {
        	e.printStackTrace();
        }
        return prop;
    }
}
