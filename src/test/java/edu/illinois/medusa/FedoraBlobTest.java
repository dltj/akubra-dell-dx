package edu.illinois.medusa;

import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/1/11
 * Time: 11:17 AM
 */
public class FedoraBlobTest extends AbstractBlobTest {

    private FedoraBlobStore store;
    private FedoraBlobStoreConnection connection;

    @BeforeClass
    private void getStore() {
        store = new FedoraBlobStore(URI.create("fedora"), "libstor.grainger.illinois.edu", "libstor.grainger.illinois.edu", "test", "test-repository");
    }

    private void openConnection() throws IOException {
        connection = store.openConnection();
    }

    protected FedoraBlob getTestBlob() {
        return connection.getBlob(URI.create("fedora-test-blob-id"), null);
    }

    @Test
    public void testFedoraMetadataAdded() throws Exception {
        openConnection();
        FedoraBlob blob = getTestBlob();
        writeBlob(getTestBytes(), blob);
        //force info call
        FedoraBlob new_blob = getTestBlob();
        new_blob.getSize();
        Assert.assertTrue(new_blob.response().scspResponse().getResponseHeaders().containsValue("x-fedora-meta-repository-name", "test-repository"));
        Assert.assertTrue(new_blob.response().scspResponse().getResponseHeaders().containsValue("x-fedora-meta-stream-id", "fedora-test-blob-id"));
        blob.delete();
    }
}
