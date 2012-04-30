package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

/**
 * BlobStore specialized to managed datastreams in a Fedora repository
 *
 * @author - Howard Ding - hding2@illinois.edu
 */

public class FedoraDatastreamBlobStore extends FedoraBlobStore {

    /**
     * Construct a new FedoraDatastreamBlobStore
     *
     * @param storeID        Arbitrary name for the blob store
     * @param configFilePath Path to properties file for configuring blob store
     */
    protected FedoraDatastreamBlobStore(URI storeID, String configFilePath) {
        super(storeID, configFilePath);
    }

    /**
     * Configure the channel of the content router. One of two different keys is used - first
     * content-router.datastream-channel and then content-router.channel if the first is not set.
     *
     * @param config Properties containing configuration information
     * @return Name of content router channel to use
     */
    protected String configContentRouterChannel(Properties config) {
        return config.getProperty("content-router.datastream-channel", config.getProperty("content-router.channel"));
    }

    /**
     * Return an iterator over the blobIds in this blob store. Assures that only managed datastream ids are returned
     * by the iterator.
     *
     * @param filterPrefix If not null then only return ids starting with this prefix
     * @return FedoraIterator over blobIds in this blob store.
     * @throws IOException
     * @throws ObjectEnumeratorException
     * @throws ScspExecutionException
     */
    protected FedoraIterator newBlobIterator(String filterPrefix) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        return new FedoraDatastreamIterator(this, filterPrefix);
    }

}
