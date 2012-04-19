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

    public static CaringoBlobStore newStore() throws Exception {
        return new CaringoBlobStore(URI.create("caringo"), TestConfig.configFileName);
    }

    @Test
    public void testGetBucketName() throws Exception {
        Assert.assertEquals(store.getBucketName(), TestConfig.getProperty("connection.bucket"));
    }

}
