package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: hading
 * Date: 3/30/12
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraObjectBlobStore extends FedoraBlobStore {

    protected FedoraObjectBlobStore(URI storeID, String repositoryName, CaringoConfigConnection connectionConfig,
                                    CaringoConfigAuthentication authenticationConfig, FedoraContentRouterConfig contentRouterConfig) {
        super(storeID, repositoryName, connectionConfig, authenticationConfig, contentRouterConfig);
    }

    protected FedoraObjectBlobStore(URI storeId, String repositoryName, CaringoConfigConnection connectionConfig,
                                  CaringoConfigAuthentication authenticationConfig)  {
        this(storeId, repositoryName, connectionConfig, authenticationConfig, null);
    }

    protected FedoraObjectBlobStore(URI storeID, String repositoryName, CaringoConfigConnection connectionConfig,
                                    FedoraContentRouterConfig contentRouterConfig) {
        this(storeID, repositoryName, connectionConfig, null, contentRouterConfig);
    }

    protected FedoraObjectBlobStore(URI storeId, String repositoryName, CaringoConfigConnection connectionConfig) {
        this(storeId, repositoryName, connectionConfig, null, null);
    }

    protected Iterator<URI> listBlobIds(String filterPrefix) throws IOException {
        try {
            return new FedoraObjectIterator(this, filterPrefix);
        } catch (ObjectEnumeratorException e) {
            throw new RuntimeException();
        } catch (ScspExecutionException e) {
            throw new RuntimeException();
        }
    }
}
