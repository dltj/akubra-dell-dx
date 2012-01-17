package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.client.ScspHeaders;
import com.caringo.client.ScspQueryArgs;
import com.caringo.client.ScspResponse;
import org.akubraproject.impl.StreamManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/1/11
 * Time: 12:27 PM
 */
public class HintedBlobStoreConnection extends CaringoBlobStoreConnection {

    protected CaringoHints hints;

    protected HintedBlobStoreConnection(HintedBlobStore owner, StreamManager streamManager, CaringoHints hints) throws IOException {
        super(owner, streamManager);
        this.hints = hints;
    }

    public HintedBlob getBlob(URI blobId, Map<String, String> hints) {
        return new HintedBlob(this, blobId, this.hints.copy_and_merge_hints(hints));
    }

}
