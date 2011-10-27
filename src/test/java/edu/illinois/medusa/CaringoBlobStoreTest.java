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
        //TODO It'd be ideal to make this configurable so the core code wouldn't have to change just to run the tests.
        //perhaps a properties.example file in VC to be moved to properties and then read it to do the tests

        //return new CaringoBlobStore(URI.create("caringo"),"cas.caringo.com", "cas.caringo.com", "uiuc");
        return new CaringoBlobStore(URI.create("caringo"),"libstor.grainger.illinois.edu", "libstor.grainger.illinois.edu", "test");
    }

    @Test
    public void testGetBucketName() throws Exception {
        Assert.assertEquals(store.getBucketName(), "test");
    }

}
