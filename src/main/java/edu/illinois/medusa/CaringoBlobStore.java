package edu.illinois.medusa;

import com.caringo.client.ScspClient;
import org.akubraproject.impl.AbstractBlobStore;
import org.akubraproject.impl.StreamManager;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.print.DocFlavor;
import javax.transaction.Transaction;

/**
 * Implement Akubra AbstractBlobStore for Caringo storage server.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class CaringoBlobStore extends AbstractBlobStore {

    /**
     * Configuration for connecting to Caringo server
     */
    protected CaringoConfigConnection connectionConfig;
    /**
     * Configuration for authenticating to Caringo server.
     */
    protected CaringoConfigAuthentication authenticationConfig;
    /**
     * Stream manager for the BlobStore
     */
    protected StreamManager streamManager;

    /**
     * The bucket used on the storage server.
     * @return Bucket name used on storage server.
     */
    public String getBucketName() {
        return connectionConfig.caringoBucket;
    }

    /**
     * The stream manager for the BlobStore.
     * @return  StreamManager for this BlobStore
     */
    public StreamManager getStreamManager() {
        return streamManager;
    }

    /**
     * Constructor with ID and configuration for connection and authentication
     * @param storeId ID for this store
     * @param connectionConfig Configuration for connection to storage server
     * @param authenticationConfig Configuration for authentication to storage server. May be null if authentication is unused.
     */
    protected CaringoBlobStore(URI storeId, CaringoConfigConnection connectionConfig, CaringoConfigAuthentication authenticationConfig) {
        super (storeId);
        this.connectionConfig = connectionConfig;
        this.authenticationConfig = authenticationConfig;
        this.streamManager = new StreamManager();
    }

    /**
     * Constructor with ID and configuration for connection. Authentication will not be used.
     * @param storeId ID for this store
     * @param connectionConfig Configuration for connection to storage server
     */
    protected CaringoBlobStore(URI storeId, CaringoConfigConnection connectionConfig) {
        this(storeId, connectionConfig, null);
    }

    /**
     * Open a new connection to this store.
     * @param tx For this BlobStore this must be null - transactions are unsupported.
     * @param hints For this BlobStore this is ignored.
     * @return  A new connection to this BlobStore
     * @throws IOException If the tx argument is not null or there is an error opening the connection.
     */
    public CaringoBlobStoreConnection openConnection(Transaction tx, Map<String, String> hints) throws IOException {
        if (tx != null) {
            throw new UnsupportedOperationException();
        }
        return new CaringoBlobStoreConnection(this, streamManager);
    }

    /**
     * Open a new connection to this store
     * @return A new connection to this BlobStore
     * @throws IOException If there is an error opening the connection.
     */
    public CaringoBlobStoreConnection openConnection() throws IOException {
        return this.openConnection(null, null);
    }

    /**
     * Get a new SDK ScspClient to Caringo storage.
     * @return A new SDK client to Caringo storage
     * @throws IOException If there is an error getting a client
     */
    public ScspClient getCaringoClient() throws IOException {
        String[] hosts = new String[1];
        hosts[0] = connectionConfig.serverURL;
        ScspClient client = new ScspClient(hosts, connectionConfig.port, connectionConfig.maxConnectionPoolSize,
                connectionConfig.maxRetries, connectionConfig.connectionTimeout, connectionConfig.poolTimeout,
                connectionConfig.locatorRetryTimeout);
        if (connectionConfig.caringoDomain != null)
            client.setHostHeaderValue(connectionConfig.caringoDomain);
        return client;
    }

    //Not implemented here, but FedoraBlobStore and its subclasses will implement
    protected Iterator<URI> listBlobIds(String filterPrefix) throws IOException {
        throw new UnsupportedOperationException("blob-id listing not supported");
    }
}
