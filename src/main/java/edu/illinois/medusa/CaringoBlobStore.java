package edu.illinois.medusa;

import com.caringo.client.ScspClient;
import org.akubraproject.impl.AbstractBlobStore;
import org.akubraproject.impl.StreamManager;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.print.DocFlavor;
import javax.transaction.Transaction;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/14/11
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoBlobStore extends AbstractBlobStore {

    protected CaringoConfigConnection connectionConfig;
    protected CaringoConfigAuthentication authenticationConfig;

    protected StreamManager streamManager;

    public String getBucketName() {
        return connectionConfig.caringoBucket;
    }

    public StreamManager getStreamManager() {
        return streamManager;
    }

    protected CaringoBlobStore(URI storeId, CaringoConfigConnection connectionConfig, CaringoConfigAuthentication authenticationConfig) {
        super (storeId);
        this.connectionConfig = connectionConfig;
        this.authenticationConfig = authenticationConfig;
        this.streamManager = new StreamManager();
    }

    protected CaringoBlobStore(URI storeId, CaringoConfigConnection connectionConfig) {
        this(storeId, connectionConfig, null);
    }

    public CaringoBlobStoreConnection openConnection(Transaction tx, Map<String, String> hints) throws IOException {
        if (tx != null) {
            throw new UnsupportedOperationException();
        }
        return new CaringoBlobStoreConnection(this, streamManager);
    }

    public CaringoBlobStoreConnection openConnection() throws IOException {
        return this.openConnection(null, null);
    }

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

}
