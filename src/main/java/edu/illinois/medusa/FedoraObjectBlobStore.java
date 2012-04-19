package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: hading
 * Date: 3/30/12
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraObjectBlobStore extends FedoraBlobStore {

    protected FedoraObjectBlobStore(URI storeID, String configFilePath) {
        super(storeID, configFilePath);
    }

    protected String configContentRouterChannel(Properties config) {
        return config.getProperty("content-router.object-channel", config.getProperty("content-router.channel"));
    }

    protected FedoraIterator newBlobIterator(String filterPrefix) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        return new FedoraObjectIterator(this, filterPrefix);
    }

}
