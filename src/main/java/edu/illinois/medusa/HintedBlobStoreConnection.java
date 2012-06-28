package edu.illinois.medusa;

import org.akubraproject.impl.StreamManager;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Extension of CaringoBlobStoreConnection that can turn hints into headers
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class HintedBlobStoreConnection extends CaringoBlobStoreConnection {

    /**
     * Hints apropos to this connection
     */
    protected CaringoHints hints;

    /**
     * Construct a new connection
     *
     * @param owner Owning blob store
     * @param streamManager StreamManager managing streams for this connection
     * @param hints Any initializing hints for this connection
     * @throws IOException If there is a problem opening the connection
     */
    protected HintedBlobStoreConnection(HintedBlobStore owner, StreamManager streamManager, CaringoHints hints) throws IOException {
        super(owner, streamManager);
        this.hints = hints;
    }

    /**
     * Get a new blob that will be serviced with this connection
     *
     * @param blobId ID for the blob
     * @param hints Ignored for CaringoBlobStoreConnection
     * @return New blob
     */
    public HintedBlob getBlob(URI blobId, Map<String, String> hints) {
        return new HintedBlob(this, blobId, this.hints.copy_and_merge_hints(hints));
    }

    protected HintCopier getHintCopier() {
        return this.owner.getHintCopier();
    }

}
