package edu.illinois.medusa;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import javax.transaction.Transaction;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/1/11
 * Time: 10:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraBlobStore extends HintedBlobStore {

    protected FedoraBlobStore(URI storeId, String hostUrl, String domainName, String bucketName, String repositoryName) {
        this(storeId, hostUrl, domainName, bucketName, repositoryName, 80, 4, 4, 120, 300, 300);
    }

    protected FedoraBlobStore(URI storeId, String hostUrl, String domainName, String bucketName, String repositoryName,
                               int port, int maxConnectionPoolSize, int maxRetries,
                               int connectionTimeout, int poolTimeout, int locatorRetryTimeout) {
        super(storeId, hostUrl, domainName, bucketName, port, maxConnectionPoolSize, maxRetries, connectionTimeout,
                poolTimeout, locatorRetryTimeout);
        this.hints.addHint("fedora:repository-name", repositoryName);
    }

    public FedoraBlobStoreConnection openConnection(Transaction tx, Map<String, String> hints) throws IOException {
        if (tx != null) {
            throw new UnsupportedOperationException();
        }
        return new FedoraBlobStoreConnection(this, streamManager, this.hints.copy_and_merge_hints(hints));
    }

    public FedoraBlobStoreConnection openConnection() throws IOException {
        return this.openConnection(null, null);
    }
}
