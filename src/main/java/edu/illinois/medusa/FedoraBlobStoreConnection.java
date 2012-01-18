package edu.illinois.medusa;

import org.akubraproject.impl.StreamManager;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Extension of HintedBlobStoreConnection with functionality specific to Fedora.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class FedoraBlobStoreConnection extends HintedBlobStoreConnection {

    /**
     * Construct a new connection
     *
     * @param owner Owning blob store
     * @param streamManager StreamManager for streams managed by this connection
     * @param hints Any hints used to initialize this connection
     * @throws IOException If there is a problem making a connection
     */
    protected FedoraBlobStoreConnection(FedoraBlobStore owner, StreamManager streamManager, CaringoHints hints) throws IOException {
        super(owner, streamManager, hints);
    }

    /**
     * Return a blob using this connection
     *
     * @param blobId ID of requested blob
     * @param hints Any hints used to initialize this blob. Combined with the hints of the connection.
     * @return New Blob with the requested id and hints.
     */
    public FedoraBlob getBlob(URI blobId, Map<String, String> hints) {
        return new FedoraBlob(this, blobId, this.hints.copy_and_merge_hints(hints));
    }
}
