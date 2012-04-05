package edu.illinois.medusa;

/**
 * The expectation is that the channel will have a definition in the DX content router rules that matches
 * the x-fedora-meta-repository-name header to some unique string for the particular repository, but no one
 * can stop you from hooking it up to any channel that you want.
 */
public class FedoraContentRouterConfig {

    protected String host;
    protected int port;
    protected String channel;

    protected FedoraContentRouterConfig(String host, int port, String channel) {
        this.host = host;
        this.port = port;
        this.channel = channel;
    }
}
