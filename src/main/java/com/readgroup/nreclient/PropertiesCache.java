package com.readgroup.nreclient;

import org.apache.logging.log4j.LogManager;

import java.io.IOException;
        import java.io.InputStream;
        import java.util.Properties;
        import java.util.Set;
import org.apache.logging.log4j.Logger;

public class PropertiesCache
{
    private final Properties configProp = new Properties();
    private static final Logger logger = LogManager.getLogger();

    private PropertiesCache()
    {
        //Private constructor to restrict new instances
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("application.properties");
        logger.info("Reading all properties from the file");
        try {
            configProp.load(in);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    //Bill Pugh Solution for singleton pattern
    private static class LazyHolder
    {
        private static final PropertiesCache INSTANCE = new PropertiesCache();
    }

    public static PropertiesCache getInstance()
    {
        return LazyHolder.INSTANCE;
    }

    public String getProperty(String key){
        return configProp.getProperty(key);
    }

    public Set<String> getAllPropertyNames(){
        return configProp.stringPropertyNames();
    }

    public boolean containsKey(String key){
        return configProp.containsKey(key);
    }
}