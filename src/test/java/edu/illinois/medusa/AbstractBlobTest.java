package edu.illinois.medusa;

import org.akubraproject.Blob;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/1/11
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBlobTest {

    protected URI testBlobName() {
        return URI.create("test-blob");
    }

    protected byte[] getTestBytes() {
        return "Test string".getBytes();
    }

    protected void writeBlob(byte[] testBytes, Blob blob) throws Exception {
        if (testBytes == null)
            testBytes = getTestBytes();
        OutputStream out = blob.openOutputStream(100, true);
        out.write(testBytes);
        out.close();
    }

    protected void deleteBlob(Blob blob) {
        try {
            blob.delete();
        } catch (IOException e) {
        }
    }

    protected abstract CaringoBlob getTestBlob();

    protected void writeTestBlob(byte[] testBytes) throws Exception {
        writeBlob(testBytes, getTestBlob());
    }

    protected void deleteTestBlob() {
        deleteBlob(getTestBlob());
    }
}
