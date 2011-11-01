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

    protected HintedBlobStore(URI storeId, String hostUrl, String domainName, String bucketName,
                               int port, int maxConnectionPoolSize, int maxRetries,
                               int connectionTimeout, int poolTimeout, int locatorRetryTimeout) {
        super(storeId, hostUrl, domainName, bucketName, port, maxConnectionPoolSize, maxRetries, connectionTimeout,
                poolTimeout, locatorRetryTimeout);
        this.hints = new CaringoHints();
    }

    protected HintedBlobStore(URI storeId, String hostUrl, String domainName, String bucketName) {
       this(storeId, hostUrl, domainName, bucketName, 80, 4, 4, 120, 300, 300);
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
