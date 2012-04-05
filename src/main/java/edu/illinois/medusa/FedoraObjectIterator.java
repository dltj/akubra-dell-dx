package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import java.io.IOException;
import java.net.URI;

public class FedoraObjectIterator extends FedoraIterator {

    protected FedoraObjectIterator(FedoraBlobStore blobStore, String filterPrefix) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        super(blobStore, filterPrefix);
    }

    //For an object stream the id will be of the form info:fedora/PID
    protected boolean acceptResponse() throws IOException {
        return (currentPID.indexOf('/') == currentPID.lastIndexOf('/')) && super.acceptResponse();
    }

}
