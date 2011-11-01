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

    protected String bucketName;
    protected String hostUrl;
    protected String domainName;
    protected int port;
    protected int maxConnectionPoolSize;
    protected int maxRetries;
    protected int connectionTimeout;
    protected int poolTimeout;
    protected int locatorRetryTimeout;

    protected StreamManager streamManager;

    public String getBucketName() {
        return bucketName;
    }

    public StreamManager getStreamManager() {
        return streamManager;
    }

    protected CaringoBlobStore(URI storeId, String hostUrl, String domainName, String bucketName) {
        this(storeId, hostUrl, domainName, bucketName, 80, 4, 4, 120, 300, 300);
    }

    protected CaringoBlobStore(URI storeId, String hostUrl, String domainName, String bucketName,
                               int port, int maxConnectionPoolSize, int maxRetries,
                               int connectionTimeout, int poolTimeout, int locatorRetryTimeout) {
        super(storeId);
        this.bucketName = bucketName;
        this.hostUrl = hostUrl;
        this.streamManager = new StreamManager();
        this.domainName = domainName;
        this.port = port;
        this.maxConnectionPoolSize = maxConnectionPoolSize;
        this.maxRetries = maxRetries;
        this.connectionTimeout = connectionTimeout;
        this.poolTimeout = poolTimeout;
        this.locatorRetryTimeout = locatorRetryTimeout;
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
        hosts[0] = hostUrl;
        ScspClient client = new ScspClient(hosts, port, maxConnectionPoolSize, maxRetries,
                connectionTimeout, poolTimeout, locatorRetryTimeout);
        if (domainName != null)
            client.setHostHeaderValue(domainName);
        return client;
    }

}
