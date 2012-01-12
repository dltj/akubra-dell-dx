package edu.illinois.medusa;

import javax.print.DocFlavor;
import javax.transaction.Transaction;
import java.awt.image.ImagingOpException;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/1/11
 * Time: 12:27 PM
 */
public class HintedBlobStore extends CaringoBlobStore {

    protected CaringoHints hints;

    protected HintedBlobStore(URI storeId, CaringoConfigConnection connectionConfig, CaringoConfigAuthentication authenticationConfig) {
        super(storeId, connectionConfig, authenticationConfig);
        this.hints = new CaringoHints();
    }

    protected HintedBlobStore(URI storeId, CaringoConfigConnection connectionConfig) {
        this(storeId, connectionConfig, null);
    }

    public HintedBlobStoreConnection openConnection(Transaction tx, Map<String, String> hints) throws IOException {
        if (tx != null) {
            throw new UnsupportedOperationException();
        }
        return new HintedBlobStoreConnection(this, streamManager, this.hints.copy_and_merge_hints(hints));
    }

    public HintedBlobStoreConnection openConnection() throws IOException {
        return this.openConnection(null, null);
    }
}
