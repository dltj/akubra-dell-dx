package edu.illinois.medusa;

/**
 * The expectation is that the channel will have a definition in the DX content router rules that matches
 * the x-fedora-meta-repository-name header to some unique string for the particular repository, but no one
 * can stop you from hooking it up to any channel that you want.
 *
 * @author - Howard Ding - hding2@illinois.edu
 */

public class FedoraContentRouterConfig {

    /**
     * Internet host for content router
     */
    protected String host;
    /**
     * Port of content router
     */
    protected int port;
    /**
     * Channel on content router
     */
    protected String channel;

    /**
     * Create a new FedoraContentRouterConfig encapsulating information for connecting to the content router
     *
     * @param host    Content router host
     * @param port    Content router port
     * @param channel Content router channel
     */
    protected FedoraContentRouterConfig(String host, int port, String channel) {
        this.host = host;
        this.port = port;
        this.channel = channel;
    }
}
