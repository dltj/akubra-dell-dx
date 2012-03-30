package edu.illinois.medusa;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: hading
 * Date: 3/30/12
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraObjectBlobStore extends FedoraBlobStore {
    protected FedoraObjectBlobStore(URI storeId, String repositoryName, CaringoConfigConnection connectionConfig,
                                  CaringoConfigAuthentication authenticationConfig)  {
        super(storeId, repositoryName, connectionConfig, authenticationConfig);
    }

    protected FedoraObjectBlobStore(URI storeId, String repositoryName, CaringoConfigConnection connectionConfig) {
        this(storeId, repositoryName, connectionConfig, null);
    }
}
