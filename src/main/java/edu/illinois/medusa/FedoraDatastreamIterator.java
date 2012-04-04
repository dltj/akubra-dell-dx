package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import java.io.IOException;
import java.net.URI;

public class FedoraDatastreamIterator extends FedoraIterator {

    protected FedoraDatastreamIterator(FedoraBlobStore blobStore) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        super(blobStore);
    }

    protected boolean acceptedResponse() {
        return true;
    }

}
