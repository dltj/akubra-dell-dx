package edu.illinois.medusa;

/**
 * Information to connect to a Caringo server
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class CaringoConfigConnection {

    /**
     * URL of server
     */
    protected String serverURL;
    /**
     * Domain (in the Caringo sense) used on the server. May be null for default domain.
     */
    protected String caringoDomain;
    /**
     * Bucket used in domain to store objects
     */
    protected String caringoBucket;
    /**
     * Caringo server port
     */
    protected int port;
    /**
     * Maximum number of connections in the pool.
     */
    protected int maxConnectionPoolSize;
    /**
     * Maximum number of retries for a request.
     */
    protected int maxRetries;
    /**
     * Connection timeout in seconds.
     */
    protected int connectionTimeout;
    /**
     * Timeout for pooled connections in seconds.
     */
    protected int poolTimeout;
    /**
     * Locator retry timeout
     */
    protected int locatorRetryTimeout;

    /**
     * Constructor with all available parameters
     * @param serverURL
     * @param caringoDomain
     * @param caringoBucket
     * @param port
     * @param maxConnectionPoolSize
     * @param maxRetries
     * @param connectionTimeout
     * @param poolTimeout
     * @param locatorRetryTimeout
     */
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

    /**
     * Simple constructor with defaults for other parameters.
     * @param serverURL
     * @param caringoDomain
     * @param caringoBucket
     */
    protected CaringoConfigConnection(String serverURL, String caringoDomain, String caringoBucket) {
        this(serverURL, caringoDomain, caringoBucket, 80, 4, 4, 120, 300, 300);
    }
}
