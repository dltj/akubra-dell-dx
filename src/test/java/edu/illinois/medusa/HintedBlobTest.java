package edu.illinois.medusa;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/1/11
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class HintedBlobTest extends AbstractBlobTest {

    private HintedBlobStore store;
    private HintedBlobStoreConnection connection;

    @BeforeClass
    private void getStore() throws Exception {
        store = new HintedBlobStore(URI.create("caringo"), TestConfig.configFileName);
    }

    private void openConnection() throws IOException {
        connection = store.openConnection();
    }

    protected HintedBlob getTestBlob() {
        return connection.getBlob(testBlobName(), null);
    }

    @Test
    public void testStoreCustomHeader() throws Exception {
        InputStream input = null;
        try {
            openConnection();
            HintedBlob blob = getTestBlob();
            blob.addHint("fedora:test-header", "test-value");
            blob.addHint("fedora:test-header", "another-test-value");
            blob.addHint(":x-fedora-meta-test-header-2", "test-value-2");
            writeBlob(getTestBytes(), blob);
            HintedBlob readBlob = getTestBlob();
            input = readBlob.openInputStream();
            Assert.assertTrue(readBlob.response().scspResponse().getResponseHeaders().containsName("x-fedora-meta-test-header"));
            Assert.assertTrue(readBlob.response().scspResponse().getResponseHeaders().containsValue("x-fedora-meta-test-header", "test-value"));
            Assert.assertTrue(readBlob.response().scspResponse().getResponseHeaders().containsValue("x-fedora-meta-test-header", "another-test-value"));
            Assert.assertTrue(readBlob.response().scspResponse().getResponseHeaders().containsValue("x-fedora-meta-test-header-2", "test-value-2"));
        } finally {
            if (input != null)
                input.close();
            deleteTestBlob();
        }
    }

    @Test
    public void testMoveMetadataWithBlob() throws Exception {
        openConnection();
        HintedBlob source_blob = getTestBlob();
        source_blob.addHint("fedora:test-key", "test-value");
        writeBlob(getTestBytes(), source_blob);
        HintedBlob new_blob = connection.getBlob(URI.create("moved-blob"), null);
        try {
            source_blob.moveTo(new_blob.getId(), null);
            //force an info call
            new_blob.getSize();
            Assert.assertTrue(new_blob.response().scspResponse().getResponseHeaders().containsValue("x-fedora-meta-test-key", "test-value"));
        } finally {
            deleteBlob(new_blob);
            deleteBlob(source_blob);
        }
    }

    @Test
    public void testConfigHeaderParsing() throws Exception {
        ArrayList<String> values = store.parseConfigHeaders("");
        Assert.assertEquals(values.size(), 0);
        values = store.parseConfigHeaders("joe");
        Assert.assertEquals(values.size(), 1);
        Assert.assertEquals(values.get(0), "joe");
        values = store.parseConfigHeaders("joe|bob||fred|pete");
        Assert.assertEquals(values.size(), 3);
        Assert.assertEquals(values.get(0), "joe");
        Assert.assertEquals(values.get(1), "bob|fred");
        Assert.assertEquals(values.get(2), "pete");
    }

    @Test
    public void testStoreConfigHeader() throws Exception {
        InputStream input = null;
        try {
            openConnection();
            HintedBlob blob = getTestBlob();
            writeBlob(getTestBytes(), blob);
            HintedBlob readBlob = getTestBlob();
            input = readBlob.openInputStream();
            Assert.assertTrue(readBlob.response().scspResponse().getResponseHeaders().containsName("x-config-meta-header"));
            Assert.assertTrue(readBlob.response().scspResponse().getResponseHeaders().containsValue("x-config-meta-header", "test-value"));
            Assert.assertTrue(readBlob.response().scspResponse().getResponseHeaders().containsValue("x-config-meta-header-2", "test-value-1"));
            Assert.assertTrue(readBlob.response().scspResponse().getResponseHeaders().containsValue("x-config-meta-header-2", "test-value-2"));
        } finally {
            if (input != null)
                input.close();
            deleteTestBlob();
        }
    }
}
