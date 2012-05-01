package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

/**
 * FedoraBlobStore subclass specialized for FOXML objects.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class FedoraObjectBlobStore extends FedoraBlobStore {

    /**
     * Construct a new FedoraObjectBlobStore with the specified name and configuration file.
     *
     * @param storeID        Arbitrary name for the blob store
     * @param configFilePath Path to properties file with configuration data for the blob store
     */
    protected FedoraObjectBlobStore(URI storeID, String configFilePath) {
        super(storeID, configFilePath);
    }

    /**
     * Configure the channel of the content router. One of two different keys is used - first
     * content-router.object-channel and then content-router.channel if the first is not set.
     *
     * @param config Properties containing configuration information
     * @return Name of content router channel to use
     */
    protected String configContentRouterChannel(Properties config) {
        return config.getProperty("content-router.object-channel", config.getProperty("content-router.channel"));
    }

    /**
     * Return a new iterator over the blobIds of this blob store.
     *
     * @param filterPrefix If not null only return blobIds starting with this string
     * @return Iterator over blobIds in this blob store
     * @throws IOException
     * @throws ObjectEnumeratorException
     * @throws ScspExecutionException
     */
    protected FedoraIterator newBlobIterator(String filterPrefix) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        return new FedoraObjectIterator(this, filterPrefix);
    }

}
