package edu.illinois.medusa;

import com.caringo.client.ScspClient;
import org.akubraproject.impl.AbstractBlobStore;
import org.akubraproject.impl.StreamManager;

import javax.management.RuntimeErrorException;
import javax.transaction.Transaction;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

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
     * Pool of caringo clients
     */
    protected ScspClient[] clientPool;
    /**
     * Number of independent clients
     */
    protected static int CLIENT_COUNT = 5;
    /**
     * which client to assign next
     */
    protected int currentClientIndex = 0;

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

    /**
     * Configure this BlobStore
     *
     * @param storeId        Arbitrary URI identifying the BlobStore
     * @param configFilePath Path to properties file containing additional configuration.
     */
    protected CaringoBlobStore(URI storeId, String configFilePath) {
        super(storeId);
        this.configStore(configFilePath);
        this.streamManager = new StreamManager();
        try {
            this.initializeClients();
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize clients");
        }
    }

    /**
     * Parse config file into Properties and then use to configure BlobStore
     *
     * @param configFilePath Path to properties file containing configuration.
     */
    protected void configStore(String configFilePath) {
        Properties config = this.loadConfigFile(configFilePath);
        this.configFromProperties(config);
    }

    /**
     * Use a parsed Properties object to configure the BlobStore. Subclasses should override this (and super call
     * it) to do additional configuration.
     *
     * @param config Properties used to configure the BlobStore
     */
    protected void configFromProperties(Properties config) {
        configConnection(config);
        configAuth(config);
    }

    /**
     * Configure the BlobStore to connect to storage
     *
     * @param config Properties containing configuration information
     */
    protected void configConnection(Properties config) {
        String host = config.getProperty("connection.host");
        String domain = config.getProperty("connection.domain");
        String bucket = config.getProperty("connection.bucket");
        if (host == null || bucket == null) {
            throw new RuntimeException("Required connection parameter for BlobStore not configured in properties file.");
        }
        this.connectionConfig = new CaringoConfigConnection(host, domain, bucket);
    }

    /**
     * Configure the BlobStore to authenticate to storage - optional, only applies if all appropriate configuration
     * parameters are set.
     *
     * @param config Properties containing configuration information
     */
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

    /**
     * Load configuration information from a file into a Properties object
     *
     * @param configFilePath Path to configuration file
     * @return Properties containing configuration information
     */
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
     */
    public ScspClient getCaringoClient() {
        ScspClient client = clientPool[currentClientIndex];
        currentClientIndex++;
        currentClientIndex %= CLIENT_COUNT;
        return client;
    }

    /**
     * Create and start an initial pool of clients
     * @throws IOException
     */
    protected void initializeClients() throws IOException {
        for (int i = 0; i < CLIENT_COUNT; i++) {
            String[] hosts = new String[1];
            hosts[0] = connectionConfig.serverURL;
            ScspClient client = new ScspClient(hosts, connectionConfig.port, connectionConfig.maxConnectionPoolSize,
                    connectionConfig.maxRetries, connectionConfig.connectionTimeout, connectionConfig.poolTimeout,
                    connectionConfig.locatorRetryTimeout);
            if (connectionConfig.caringoDomain != null)
                client.setHostHeaderValue(connectionConfig.caringoDomain);
            client.start();
            clientPool[i] = client;
        }
    }

    /**
     * Return an iterator over all BlobIds for the Blobs managed by this store. Not implemented by this class,
     * but may be by subclasses.
     *
     * @param filterPrefix If not null then only return BlobIds starting with this string.
     * @return Iterator over BlobIds for Blobs in this store, filtered by filterPrefix
     * @throws IOException                   If there is an error iterating
     * @throws UnsupportedOperationException If this BlobStore does not support listing BlobIds
     */
    protected Iterator<URI> listBlobIds(String filterPrefix) throws IOException {
        throw new UnsupportedOperationException("blob-id listing not supported");
    }
}
