package edu.illinois.medusa;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: hading
 * Date: 4/19/12
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestConfig {
    public static final String configFileName = "src/test/java/edu/illinois/medusa/test-config.properties";

    public static Properties properties() throws Exception {
        Properties properties = new Properties();
        FileInputStream propertyStream = null;
        try {
            propertyStream = new FileInputStream(configFileName);
            properties.load(propertyStream);
        } finally {
            if (propertyStream != null)
                propertyStream.close();
        }
        return properties;
    }

    public static String getProperty(String key) throws Exception {
        return properties().getProperty(key);
    }
}
