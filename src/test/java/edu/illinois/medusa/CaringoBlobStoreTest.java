package edu.illinois.medusa;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/19/11
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoBlobStoreTest {

    private CaringoBlobStore store;
    protected static Properties properties;

    @BeforeMethod
    public void setUp() throws Exception {
        store = newStore();
    }

    public static void ensureProperties() throws Exception {
        if (properties == null) {
            properties = new Properties();
            FileInputStream propertyStream = new FileInputStream("src/test/java/edu/illinois/medusa/test-config.properties");
            properties.load(propertyStream);
            propertyStream.close();
        }
    }

    public static CaringoBlobStore newStore() throws Exception {
        return new CaringoBlobStore(URI.create("caringo"), caringoConfigConnection(), caringoConfigAuthentication());
    }

    public static CaringoConfigAuthentication caringoConfigAuthentication() throws Exception {
        ensureProperties();
        return new CaringoConfigAuthentication(properties.getProperty("auth.user"), properties.getProperty("auth.password"),
                properties.getProperty("auth.realm"));
    }

    public static CaringoConfigConnection caringoConfigConnection() throws Exception {
        ensureProperties();
        return new CaringoConfigConnection(properties.getProperty("conn.url"), properties.getProperty("conn.caringo_domain"),
                properties.getProperty("conn.caringo_bucket"));
    }

    @Test
    public void testGetBucketName() throws Exception {
        ensureProperties();
        Assert.assertEquals(store.getBucketName(), properties.getProperty("conn.caringo_bucket"));
    }

}
