package edu.illinois.medusa;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/19/11
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoBlobStoreTest {

    private CaringoBlobStore store;

    @BeforeMethod
    public void setUp() throws Exception {
        store = newStore();
    }

    public static CaringoBlobStore newStore() {
        return new CaringoBlobStore(URI.create("caringo"),"cas.caringo.com", "cas.caringo.com", "uiuc");
    }

    @Test
    public void testGetBucketName() throws Exception {
        Assert.assertEquals(store.getBucketName(), "uiuc");
    }

}
