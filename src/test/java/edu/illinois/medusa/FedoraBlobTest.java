package edu.illinois.medusa;

import junit.framework.Assert;
import org.apache.commons.codec.binary.Base64;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;

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
    private void getStore() throws Exception {
        store = new FedoraBlobStore(URI.create("fedora"), TestConfig.configFileName);
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
        Assert.assertTrue(new_blob.response().scspResponse().getResponseHeaders().containsValue("x-fedora-meta-repository-name", TestConfig.getProperty("store.repository-name")));
        Assert.assertTrue(new_blob.response().scspResponse().getResponseHeaders().containsValue("x-fedora-meta-stream-id", "fedora-test-blob-id"));
        blob.delete();
    }

    @Test
    public void testMD5Metadata() throws Exception {
        openConnection();
        FedoraBlob blob = getTestBlob();
        String content = "Some test bytes";
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(content.getBytes());
        byte[] md5_bytes = md.digest();
        String base64 = new String(Base64.encodeBase64(md5_bytes));
        writeBlob(content.getBytes(), blob);
        FedoraBlob readBlob = getTestBlob();
        readBlob.openInputStream();
        Assert.assertTrue(readBlob.response().scspResponse().getResponseHeaders().containsValue("Content-MD5", base64));
    }
}
