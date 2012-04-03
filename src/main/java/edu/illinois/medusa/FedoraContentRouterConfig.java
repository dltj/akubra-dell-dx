package edu.illinois.medusa;

/**
 * Created with IntelliJ IDEA.
 * User: hading
 * Date: 4/3/12
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
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
