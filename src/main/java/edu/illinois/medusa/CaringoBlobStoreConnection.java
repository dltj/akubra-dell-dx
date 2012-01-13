package edu.illinois.medusa;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/14/11
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */

import com.caringo.client.*;
import org.akubraproject.impl.AbstractBlobStoreConnection;
import org.akubraproject.impl.StreamManager;

import java.io.*;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

public class CaringoBlobStoreConnection extends AbstractBlobStoreConnection {

    protected static final FileWriter logFile = makeLog();

    protected static FileWriter makeLog() {
        try {
            return new FileWriter(new File("/tmp/fedora-access.log"));
        } catch (Exception e) {
            return null;
        }
    }

    protected CaringoBlobStore owner;
    protected ScspClient caringoClient;

    protected CaringoBlobStoreConnection(CaringoBlobStore owner, StreamManager streamManager) throws IOException {
        super(owner, streamManager);
        this.owner = owner;
        this.caringoClient = owner.getCaringoClient();
        this.caringoClient.start();
    }

    public CaringoBlob getBlob(URI blobId, Map<String, String> hints) {
        //use URI to lookup blob from Caringo server
        //for now no use of hints
        return new CaringoBlob(this, blobId);
    }

    //TODO This may be possible. I think that there needs to be DX Content Router instance that has set up a channel
    //publishing all items in the bucket. Then we can use the Enumerator API to connect to this publisher and
    //get metadata for all the items in the bucket. However, I'm not totally sure all of that goes. We'll want
    //to figure this out in order to allow fedora rebuilding, but that may wait until we have our local cluster
    //working.
    public Iterator<URI> listBlobIds(String filterPrefix) {
        throw new UnsupportedOperationException("blob-id listing not supported");
    }

    public void sync() {
        throw new UnsupportedOperationException("sync'ing not supported");
    }

    public CaringoInfoResponse info(URI id) throws IOException {
        try {
            ensureOpen();
            ScspResponse response = this.getCaringoClient().info("", objectPath(id), new ScspQueryArgs(), headersWithAuth());
            return new CaringoInfoResponse(response);
        } catch (ScspExecutionException e) {
            throw new IOException();
        }
    }

    public CaringoReadResponse read(URI id) throws IOException {
        FileOutputStream output = null;
        CaringoReadResponse caringoReadResponse;
        try {
            ensureOpen();
            //File tmpFile = File.createTempFile("fedora-input-" + id.toString(), ".blob");
            File tmpFile = File.createTempFile("fedora-input" , ".blob");
            tmpFile.deleteOnExit();
            output = new FileOutputStream(tmpFile);
            ScspResponse response = this.getCaringoClient().read("", objectPath(id), output,
                    new ScspQueryArgs(), headersWithAuth());
            caringoReadResponse = new CaringoReadResponse(response, tmpFile);
        } catch (ScspExecutionException e) {
            throw new IOException();
        } finally {
            if (output != null)
                output.close();
        }
        return caringoReadResponse;
    }

    public CaringoDeleteResponse delete(URI id) throws IOException {
        try {
            ensureOpen();
            ScspResponse response = this.getCaringoClient().delete("", objectPath(id), new ScspQueryArgs(),
                    headersWithAuth());
            return new CaringoDeleteResponse(response);

        } catch (ScspExecutionException e) {
            throw new IOException();
        }
    }

    public CaringoWriteResponse write(URI id, CaringoOutputStream outputStream, boolean overwrite) throws IOException {
        InputStream input = null;
        try {
            ensureOpen();
            Long size = outputStream.size();
            input = outputStream.contentStream();
            ScspResponse
                    response = this.getCaringoClient().write(objectPath(id), input, size, new ScspQueryArgs(), headersWithAuth());
            return new CaringoWriteResponse(response);
        } catch (ScspExecutionException e) {
            throw new IOException();
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    protected ScspHeaders headersWithAuth() {
        ScspHeaders headers = new ScspHeaders();
        if (this.owner.authenticationConfig != null) {
            headers.setAuthentication(this.owner.authenticationConfig.scspAuth());
        }
        return headers;
    }

    public String bucketName() {
        return owner.getBucketName();
    }

    protected StreamManager getStreamManager() {
        return this.owner.getStreamManager();
    }

    //Return caringo path incorporating bucket
    protected String objectPath(URI id) {
        String path = "/" + this.bucketName() + "/" + id.toString();
        try {
            logFile.write(path);
            logFile.write("\n");
            logFile.flush();
        } catch (Exception e) {
        }
        return path;
    }

    protected void ensureOpen() {
        try {
            if (!this.caringoClient.isStarted()) {
                this.caringoClient.start();
                closed = false;
            }
        } catch (IOException e) {
            throw new IllegalStateException();
        }
        super.ensureOpen();
    }

    public boolean isClosed() {
        if (!this.caringoClient.isStarted())
            closed = true;
        return super.isClosed();
    }

    public void close() {
        if (this.caringoClient.isStarted())
            this.caringoClient.stop();
        super.close();
    }

    protected ScspClient getCaringoClient() {
        return this.caringoClient;
    }
}
