package edu.illinois.medusa;

import javax.transaction.Transaction;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Blob Store that can use Akubra hints to maintain Caringo headers
 *
 * @author Howard Ding - hding2@illinois.edu
 */

public class HintedBlobStore extends CaringoBlobStore {

    /**
     * Hints to be used over the whole store
     */
    protected CaringoHints hints;

    protected HintedBlobStore(URI storeId, String configFilePath) {
        super(storeId, configFilePath);
        this.hints = new CaringoHints();
    }

    /**
     * Open a connection to Caringo storage
     *
     * @param tx For this BlobStore this must be null - transactions are unsupported.
     * @param hints Any hints to initialize this connection
     * @return new connection
     * @throws IOException If there is any problem opening the connection or tx is not null.
     */
    public HintedBlobStoreConnection openConnection(Transaction tx, Map<String, String> hints) throws IOException {
        if (tx != null) {
            throw new UnsupportedOperationException();
        }
        return new HintedBlobStoreConnection(this, streamManager, this.hints.copy_and_merge_hints(hints));
    }

    /**
     * Open a connection to Caringo storage
     *
     * @return new connection
     * @throws IOException If there is any problem opening the connection
     */
    public HintedBlobStoreConnection openConnection() throws IOException {
        return this.openConnection(null, null);
    }
}
