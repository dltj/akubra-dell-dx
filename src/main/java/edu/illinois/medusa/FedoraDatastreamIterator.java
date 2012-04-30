package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import java.io.IOException;

/**
 * Iterator over managed datastreams in a FedoraBlobStore.
 *
 * @author Howard Ding - hding2@illinois.edu
 */

public class FedoraDatastreamIterator extends FedoraIterator {

    /**
     * Construct a new FedoraDatastreamIterator
     *
     * @param blobStore    FedoraBlobStore on which to construct iterator
     * @param filterPrefix If not null then only include blobIds beginning with this prefix
     * @throws IOException
     * @throws ObjectEnumeratorException
     * @throws ScspExecutionException
     */
    protected FedoraDatastreamIterator(FedoraBlobStore blobStore, String filterPrefix) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        super(blobStore, filterPrefix);
    }

    /**
     * Add condition to super method, requiring that id of blob have the right form for a managed datastream
     * for the iterator to report it.
     *
     * @return
     * @throws IOException
     */
    protected boolean acceptResponse() throws IOException {
        //for a datastream the stream id will be of the form info:fedora/PID/DSID/DSID.VERSION - the test below is
        //enough to distinguish this from an object datastream
        return (currentPID.indexOf('/') != currentPID.lastIndexOf('/')) && super.acceptResponse();
    }

}
