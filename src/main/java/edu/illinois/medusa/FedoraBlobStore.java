package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import javax.transaction.Transaction;

/**
 * Extension of HintedBlobStore with functionality appropriate to Fedora. Creates connections of appropriate class.
 * Stores name of repository internally and passes along to be stored with each object.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class FedoraBlobStore extends HintedBlobStore {

    protected FedoraContentRouterConfig contentRouterConfig;
    protected String repositoryName;

    protected FedoraBlobStore(URI storeID, String repositoryName, CaringoConfigConnection connectionConfig,
                              CaringoConfigAuthentication authenticationConfig, FedoraContentRouterConfig contentRouterConfig) {
        super(storeID, connectionConfig, authenticationConfig);
        this.contentRouterConfig = contentRouterConfig;
        this.repositoryName = repositoryName;
        this.hints.addHint("fedora:repository-name", repositoryName);
    }

    /**
     * Construct a new blob store with full configuration options.
     *
     * @param storeId ID to give to this store
     * @param repositoryName Name of Fedora repository. Stored in Caringo metadata.
     * @param connectionConfig Configuration for connection to Caringo storage
     * @param authenticationConfig Configuration for authentication to Caringo storage. May be null.
     */
    protected FedoraBlobStore(URI storeId, String repositoryName, CaringoConfigConnection connectionConfig,
                              CaringoConfigAuthentication authenticationConfig) {
        this(storeId, repositoryName, connectionConfig, authenticationConfig, null);
    }

    protected FedoraBlobStore(URI storeId, String repositoryName, CaringoConfigConnection connectionConfig,
                              FedoraContentRouterConfig contentRouterConfig) {
        this(storeId, repositoryName, connectionConfig, null, contentRouterConfig);
    }

    /**
     * Construct a new blob store without authentication.
     *
     * @param storeId ID to give to this store
     * @param repositoryName Name of Fedora repository. Stored in Caringo metadata.
     * @param connectionConfig Configuration for connection to Caringo storage
     */
    protected FedoraBlobStore(URI storeId, String repositoryName, CaringoConfigConnection connectionConfig) {
        this(storeId, repositoryName, connectionConfig, null, null);
    }

    /**
     * Return a new connection to the blob store
     *
     * @param tx Transaction. Must be null for this store.
     * @param hints Hints to initialize connection
     * @return New connection to blob store
     * @throws IOException If there is an error creating the connection
     */
    public FedoraBlobStoreConnection openConnection(Transaction tx, Map<String, String> hints) throws IOException {
        if (tx != null) {
            throw new UnsupportedOperationException();
        }
        return new FedoraBlobStoreConnection(this, streamManager, this.hints.copy_and_merge_hints(hints));
    }

    /**
     * Return a new connection to the blob store
     *
     * @return New connection to blob store
     * @throws IOException If there is a problem creating the connection
     */
    public FedoraBlobStoreConnection openConnection() throws IOException {
        return this.openConnection(null, null);
    }

    protected FedoraContentRouterConfig getContentRouterConfig() {
        return this.contentRouterConfig;
    }

    protected Iterator<URI> listBlobIds(String filterPrefix) throws IOException {
        try {
            return new FedoraIterator(this, filterPrefix);
        } catch (ObjectEnumeratorException e) {
            throw new RuntimeException();
        } catch (ScspExecutionException e) {
            throw new RuntimeException();
        }
    }

}
