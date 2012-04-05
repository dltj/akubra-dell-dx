package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import java.io.IOException;
import java.net.URI;

public class FedoraDatastreamIterator extends FedoraIterator {

    protected FedoraDatastreamIterator(FedoraBlobStore blobStore, String filterPrefix) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        super(blobStore, filterPrefix);
    }

    //for a datastream the stream id will be of the form info:fedora/PID/DSID/DSID.VERSION
    protected boolean acceptResponse() throws IOException {
        return (currentPID.indexOf('/') != currentPID.lastIndexOf('/')) && super.acceptResponse();
    }

}
