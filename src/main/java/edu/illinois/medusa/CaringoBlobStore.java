package edu.illinois.medusa;

import com.caringo.client.ScspClient;
import org.akubraproject.impl.AbstractBlobStore;
import org.akubraproject.impl.StreamManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
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
     *
     * @return Bucket name used on storage server.
     */
    public String getBucketName() {
        return connectionConfig.caringoBucket;
    }

    /**
     * The stream manager for the BlobStore.
     *
     * @return StreamManager for this BlobStore
     */
    public StreamManager getStreamManager() {
        return streamManager;
    }

    protected CaringoBlobStore(URI storeId, String configFilePath) {
        super(storeId);
        this.configStore(configFilePath);
        this.streamManager = new StreamManager();
    }

    protected void configStore(String configFilePath) {
        Properties config = this.loadConfigFile(configFilePath);
        this.configFromProperties(config);
    }

    //This does the actual configuration work and should be overridden in subclasses for any additional work
    //that they want to do.
    protected void configFromProperties(Properties config) {
        configConnection(config);
        configAuth(config);
    }

    protected void configConnection(Properties config) {
        String host = config.getProperty("connection.host");
        String domain = config.getProperty("connection.domain");
        String bucket = config.getProperty("connection.bucket");
        if (host == null || bucket == null) {
            throw new RuntimeException("Required connection parameter for BlobStore not configured in properties file.");
        }
        this.connectionConfig = new CaringoConfigConnection(host, domain, bucket);
    }

    protected void configAuth(Properties config) {
        String user = config.getProperty("authentication.user");
        String password = config.getProperty("authentication.password");
        String realm = config.getProperty("authentication.realm");
        //Unless all these are set we assume that authentication is not desired
        if (user == null || password == null || realm == null) {
            this.authenticationConfig = null;
        } else {
            this.authenticationConfig = new CaringoConfigAuthentication(user, password, realm);
        }
    }

    protected Properties loadConfigFile(String configFilePath) {
        try {
            Properties properties = new Properties();
            FileInputStream propertyStream = null;
            try {
                propertyStream = new FileInputStream(configFilePath);
                properties.load(propertyStream);
            } finally {
                if (propertyStream != null)
                    propertyStream.close();
            }
            return properties;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Akubra-Caringo config file " + configFilePath + " not found.");
        } catch (IOException e) {
            throw new RuntimeException("IOException initializing Akubra BlobStore");
        }
    }

    /**
     * Open a new connection to this store.
     *
     * @param tx    For this BlobStore this must be null - transactions are unsupported.
     * @param hints For this BlobStore this is ignored.
     * @return A new connection to this BlobStore
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
     *
     * @return A new connection to this BlobStore
     * @throws IOException If there is an error opening the connection.
     */
    public CaringoBlobStoreConnection openConnection() throws IOException {
        return this.openConnection(null, null);
    }

    /**
     * Get a new SDK ScspClient to Caringo storage.
     *
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
