package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import java.io.IOException;
import java.net.URI;

public class FedoraDatastreamIterator extends FedoraIterator {

    protected FedoraDatastreamIterator(FedoraBlobStore blobStore) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        super(blobStore);
    }

    //for a datastream the stream id will be of the form info:fedora/PID/DSID/DSID.VERSION
    protected boolean acceptResponse() {
        return (currentStreamID.indexOf('/') != currentStreamID.lastIndexOf('/')) && super.acceptResponse();
    }

}
