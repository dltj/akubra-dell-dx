package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
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

    protected FedoraBlobStore(URI storeID, String configFilePath) {
        super(storeID, configFilePath);
        this.hints.addHint("fedora:repository-name", repositoryName);
    }

    protected void configFromProperties(Properties config) {
        super.configFromProperties(config);
        this.configRepositoryName(config);
        this.configContentRouter(config);
    }

    protected void configRepositoryName(Properties config) {
        String name = config.getProperty("store.repository-name");
        if (name == null)
            throw new RuntimeException("Repository name not set in akubra-caringo configuration.");
        this.repositoryName = name;
    }

    protected void configContentRouter(Properties config) {
        String host = config.getProperty("content-router.host");
        String port = config.getProperty("content-router.port");
        String channel = this.configContentRouterChannel(config);
        if (host == null || port == null || channel == null) {
            this.contentRouterConfig = null;
        } else {
            this.contentRouterConfig = new FedoraContentRouterConfig(host, Integer.parseInt(port), channel);
        }
    }

    //override in subclasses to have more control over channel
    protected String configContentRouterChannel(Properties config) {
        return config.getProperty("content-router.channel");
    }

    /**
     * Return a new connection to the blob store
     *
     * @param tx    Transaction. Must be null for this store.
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
            return newBlobIterator(filterPrefix);
        } catch (ObjectEnumeratorException e) {
            throw new RuntimeException();
        } catch (ScspExecutionException e) {
            throw new RuntimeException();
        }
    }

    protected FedoraIterator newBlobIterator(String filterPrefix) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        return new FedoraIterator(this, filterPrefix);
    }

}
