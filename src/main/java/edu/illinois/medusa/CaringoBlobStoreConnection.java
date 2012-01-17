package edu.illinois.medusa;

/**
 * Implement Akubra AbstractBlobStoreConnection for Caringo storage
 *
 * @author Howard Ding - hding2@illinois.edu
 */

import com.caringo.client.*;
import org.akubraproject.impl.AbstractBlobStoreConnection;
import org.akubraproject.impl.StreamManager;

import java.io.*;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

public class CaringoBlobStoreConnection extends AbstractBlobStoreConnection {

    /**
     * Owning CaringoBlobStore
     */
    protected CaringoBlobStore owner;
    /**
     * SDK client used to communicate with Caringo storage server
     */
    protected ScspClient caringoClient;

    /**
     * Construct a new connection.
     * @param owner Owning blob store
     * @param streamManager Stream manager for streams used by this connection
     * @throws IOException If there is a problem instantiating the connection
     */
    protected CaringoBlobStoreConnection(CaringoBlobStore owner, StreamManager streamManager) throws IOException {
        super(owner, streamManager);
        this.owner = owner;
        this.caringoClient = owner.getCaringoClient();
        this.caringoClient.start();
    }

    /**
     * Return a blob which will be manipulated via this connection
     * @param blobId ID for the blob
     * @param hints Ignored for CaringoBlobStoreConnection
     * @return Blob with specified ID
     */
    public CaringoBlob getBlob(URI blobId, Map<String, String> hints) {
        //use URI to lookup blob from Caringo server
        //for now no use of hints
        return new CaringoBlob(this, blobId);
    }

    /**
     * Not implemented by this connection class.
     * @param filterPrefix
     * @return
     */
    //TODO This may be possible. I think that there needs to be DX Content Router instance that has set up a channel
    //publishing all items in the bucket. Then we can use the Enumerator API to connect to this publisher and
    //get metadata for all the items in the bucket. However, I'm not totally sure all of that goes. We'll want
    //to figure this out in order to allow fedora rebuilding, but that may wait until we have our local cluster
    //working.
    public Iterator<URI> listBlobIds(String filterPrefix) {
        throw new UnsupportedOperationException("blob-id listing not supported");
    }

    /**
     * Not supported by this connection class
     */
    public void sync() {
        throw new UnsupportedOperationException("sync'ing not supported");
    }

    /**
     * Do an info request for the specified storage URI
     * @param id ID of stored object
     * @return Response from info request
     * @throws IOException If there is an error executing the info request
     */
    public CaringoInfoResponse info(URI id) throws IOException {
        try {
            ensureOpen();
            ScspResponse response = this.getCaringoClient().info("", objectPath(id), new ScspQueryArgs(), headersWithAuth());
            return new CaringoInfoResponse(response);
        } catch (ScspExecutionException e) {
            throw new IOException();
        }
    }

    /**
     * Do a read request for the specified storage URI
     * @param id ID of stored object
     * @return Response from read request
     * @throws IOException If there is an error executing the read request
     */
    public CaringoReadResponse read(URI id) throws IOException {
        FileOutputStream output = null;
        CaringoReadResponse caringoReadResponse;
        try {
            ensureOpen();
            File tmpFile = File.createTempFile("fedora-input", ".blob");
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

    /**
     * Do a delete request for the specified storage URI
     * @param id ID of stored object
     * @return Response from delete request
     * @throws IOException If there is an error executing the delete request
     */
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

    /**
     * Do a write request for the specified storage URI with hints.
     * @param id ID to which to store object
     * @param outputStream CaringoOutputStream with bytes to store
     * @param overwrite If it is allowed to overwrite an existing object with the same ID
     * @param hints Hints for augmenting ScspHeaders - unused by this class, but available for subclasses
     * @return Response to write request
     * @throws IOException If there is an error executing the write request
     */
    public CaringoWriteResponse write(URI id, CaringoOutputStream outputStream, boolean overwrite, CaringoHints hints) throws IOException {
        InputStream input = null;
        try {
            ensureOpen();
            Long size = outputStream.size();
            input = outputStream.contentStream();
            ScspHeaders headers = writeHeadersWithAuth();
            augmentScspHeaders(headers, hints);
            ScspResponse response = this.getCaringoClient().write(objectPath(id), input, size, new ScspQueryArgs(), headers);
            return new CaringoWriteResponse(response);
        } catch (ScspExecutionException e) {
            throw new IOException();
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    /**
     * Do a write request for the specified storage URI without hints.
     * @param id ID to which to store object
     * @param outputStream CaringoOutputStream with bytes to store
     * @param overwrite If it is allowed to overwrite an existing object with the same ID
     * @return Response to write request
     * @throws IOException If there is an error executing the write request
     */
    public CaringoWriteResponse write(URI id, CaringoOutputStream outputStream, boolean overwrite) throws IOException {
        return write(id, outputStream, overwrite, null);
    }

    /**
     * Hook for subclasses to use hints to inject additional ScspHeaders.
     * @param headers ScspHeaders to be modified
     * @param hints CaringoHints that will modify headers
     */
    protected void augmentScspHeaders(ScspHeaders headers, CaringoHints hints) {
        if (hints != null) {
            hints.augmentScspHeaders(headers);
        }
    }

    /**
     * Headers to use for SDK request. Adds auth header if authentication is configured.
     * @return ScspHeaders for request
     */
    protected ScspHeaders headersWithAuth() {
        ScspHeaders headers = new ScspHeaders();
        if (this.owner.authenticationConfig != null) {
            headers.setAuthentication(this.owner.authenticationConfig.scspAuth());
        }
        return headers;
    }

    /**
     * Headers to use for SDK write request. If there is authentication then we set authorization on the object
     * to be written so that it may only be operated on by the realm represented by the authentication. This applies
     * to all HTTP verbs.
     * @return ScspHeaders for write request.
     */
    protected ScspHeaders writeHeadersWithAuth() {
        ScspHeaders headers = headersWithAuth();
        if (this.owner.authenticationConfig != null) {
            ScspAuthorization authorization = new ScspAuthorization();
            authorization.addAuthorization(ScspAuthorization.ALL_OP, this.owner.authenticationConfig.realm);
            headers.addValue("Castor-Authorization", authorization.getAuthSpec());
            headers.addValue("Castor-Stream-Type", "admin");
        }

        return headers;
    }

    /**
     * The bucket name used by this connection.
     * @return Bucket name used by this connection.
     */
    public String bucketName() {
        return owner.getBucketName();
    }

    /**
     * Stream manager used by this connection.
     * @return StreamManager used by this connection
     */
    protected StreamManager getStreamManager() {
        return this.owner.getStreamManager();
    }

    /**
     * Full path in the Caringo storage. We use the bucket name and the ID of the object to make the full path.
     * @param id ID for the object inside the bucket
     * @return Full ID for the Caringo server to use
     */
    //Return caringo path incorporating bucket
    protected String objectPath(URI id) {
        return "/" + this.bucketName() + "/" + id.toString();
    }

    /**
     * Make sure that the connection is open. Open if not.
     */
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

    /**
     * Whether the connection is closed.
     * @return Whether this connection is closed
     */
    public boolean isClosed() {
        if (!this.caringoClient.isStarted())
            closed = true;
        return super.isClosed();
    }

    /**
     * Close this connection
     */
    public void close() {
        if (this.caringoClient.isStarted())
            this.caringoClient.stop();
        super.close();
    }

    /**
     * The SDK client used to interact with Caringo storage
     * @return ScspClient used to interact with Caringo storage
     */
    protected ScspClient getCaringoClient() {
        return this.caringoClient;
    }
}
