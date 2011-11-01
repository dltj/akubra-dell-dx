package edu.illinois.medusa;

import org.akubraproject.impl.StreamManager;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/1/11
 * Time: 10:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraBlobStoreConnection extends HintedBlobStoreConnection {

    protected FedoraBlobStoreConnection(FedoraBlobStore owner, StreamManager streamManager, CaringoHints hints) throws IOException {
        super(owner, streamManager, hints);
    }

    public FedoraBlob getBlob(URI blobId, Map<String, String> hints) {
        return new FedoraBlob(this, blobId, this.hints.copy_and_merge_hints(hints));
    }
}
