package edu.illinois.medusa;

import java.util.Properties;

/**
 * Information to connect to a Caringo server and to use locator
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class CaringoConfigConnection {

    /**
     * URL(s) or IP(s) of server - comma separated list
     */
    protected String hosts;
    /**
     * Domain (in the Caringo sense) used on the server. May be null for default domain.
     */
    protected String domain;
    /**
     * Bucket used in domain to store objects
     */
    protected String bucket;
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
    protected int locatorRetryTime;
    /**
     * Name of locator to use
     */
    protected String locatorType;
    /**
     * Used for Proxy or Zeroconf locators
     */
    protected String clusterName;
    /**
     *  For Proxy locator address of the proxy
     */
    protected String proxyAddress;
    /**
     * For Proxy locator the port of the proxy
     */
    protected int proxyPort;
    /**
     * For Proxy locator the retry time
     */
    protected int proxyRetryTime;

    /**
     * Parse provided properties and return object holding corresponding configuration with defaults applied
     * as necessary and possible. Note that the connection.bucket must always be specified - what other parameters
     * are required depends on the particular locator and the checks will be done as that is constructed.
     *
     * @param properties
     */
    protected CaringoConfigConnection(Properties properties) {
        this.hosts = properties.getProperty("connection.host");
        this.domain = properties.getProperty("connection.domain");
        this.bucket = properties.getProperty("connection.bucket");
        if (this.bucket == null) {
            throw new RuntimeException("connection.bucket must be specified in akubra plugin properties file.");
        }
        this.port = new Integer(properties.getProperty("connection.port", "80"));
        this.maxConnectionPoolSize = new Integer(properties.getProperty("connection.max_connection_pool_size", "4"));
        this.maxRetries = new Integer(properties.getProperty("connection.max_retries", "4"));
        this.connectionTimeout = new Integer(properties.getProperty("connection.connection_timeout", "120"));
        this.poolTimeout = new Integer(properties.getProperty("connection.pool_timeout", "1"));
        this.locatorRetryTime = new Integer(properties.getProperty("connection.locator_retry_time", "300"));
        this.locatorType = properties.getProperty("connection.locator_type", "static");
        this.clusterName = properties.getProperty("connection.cluster_name");
        this.proxyAddress = properties.getProperty("connection.proxy_address");
        this.proxyPort = new Integer(properties.getProperty("connection.proxy_port", "80"));
        this.proxyRetryTime = new Integer(properties.getProperty("connection.proxy_retry_time", "300"));
    }

}
