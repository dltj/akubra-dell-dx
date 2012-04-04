package edu.illinois.medusa;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: hading
 * Date: 3/30/12
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraDatastreamBlobStore extends FedoraBlobStore {

    protected FedoraDatastreamBlobStore(URI storeId, String repositoryName, CaringoConfigConnection connectionConfig,
                                      CaringoConfigAuthentication authenticationConfig, FedoraContentRouterConfig contentRouterConfig) {
        super(storeId, repositoryName, connectionConfig, authenticationConfig, contentRouterConfig);
    }

    protected FedoraDatastreamBlobStore(URI storeId, String repositoryName, CaringoConfigConnection connectionConfig,
                                  CaringoConfigAuthentication authenticationConfig)  {
        this(storeId, repositoryName, connectionConfig, authenticationConfig, null);
    }

    protected FedoraDatastreamBlobStore(URI storeId, String repositoryName, CaringoConfigConnection connectionConfig,
                                        FedoraContentRouterConfig contentRouterConfig) {
        this(storeId, repositoryName, connectionConfig, null, contentRouterConfig);
    }

    protected FedoraDatastreamBlobStore(URI storeId, String repositoryName, CaringoConfigConnection connectionConfig) {
        this(storeId, repositoryName, connectionConfig, null, null);
    }
}
