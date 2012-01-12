package edu.illinois.medusa;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 1/12/12
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoConfigConnection {

    protected String serverURL;
    protected String caringoDomain;
    protected String caringoBucket;
    protected int port;
    protected int maxConnectionPoolSize;
    protected int maxRetries;
    protected int connectionTimeout;
    protected int poolTimeout;
    protected int locatorRetryTimeout;

    protected CaringoConfigConnection(String serverURL, String caringoDomain, String caringoBucket, int port,
                                      int maxConnectionPoolSize, int maxRetries, int connectionTimeout,
                                      int poolTimeout, int locatorRetryTimeout) {
        this.serverURL = serverURL;
        this.caringoDomain = caringoDomain;
        this.caringoBucket = caringoBucket;
        this.port = port;
        this.maxConnectionPoolSize = maxConnectionPoolSize;
        this.maxRetries = maxRetries;
        this.connectionTimeout = connectionTimeout;
        this.poolTimeout = poolTimeout;
        this.locatorRetryTimeout = locatorRetryTimeout;
    }

    protected CaringoConfigConnection(String serverURL, String caringoDomain, String caringoBucket) {
        this(serverURL, caringoDomain, caringoBucket, 80, 4, 4, 120, 300, 300);
    }
}
