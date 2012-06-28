package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.ObjectEnumeratorException;

import javax.transaction.Transaction;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Extension of HintedBlobStore with functionality appropriate to Fedora. Creates connections of appropriate class.
 * Stores name of repository internally and passes along to be stored with each object.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class FedoraBlobStore extends HintedBlobStore {

    /**
     * Configuration related to the content router and blobId enumeration
     */
    protected FedoraContentRouterConfig contentRouterConfig;
    /**
     * Repository name to be used in x-fedora-meta-repository-name header
     */
    protected String repositoryName;

    /**
     * Construct a new FedoraBlobStore
     *
     * @param storeID        Arbitrary identifier for the FedoraBlobStore
     * @param configFilePath Path to properties file for configuration FedoraBlobStore
     */
    protected FedoraBlobStore(URI storeID, String configFilePath) {
        super(storeID, configFilePath);
        this.hints.addHint("fedora:repository-name", repositoryName);
        this.addOriginatorHeaders();
    }

    protected void initializeHintCopier() {
        super.initializeHintCopier();
        this.hintCopier.addRuleFront(new HintCopyRegexpRule("reject-stream-id", false, "^x-fedora-meta-stream-id$"));
        this.hintCopier.addRuleFront(new HintCopyRegexpRule("reject-repository-name", false, "^x-fedora-meta-repository-name$"));
    }

    /**
     * Configure FedoraBlobStore from configuration Properties.
     *
     * @param config Properties containing configuration information
     */
    protected void configFromProperties(Properties config) {
        super.configFromProperties(config);
        this.configRepositoryName(config);
        this.configContentRouter(config);
    }

    /**
     * Configure the repository name to go in x-fedora-meta-repository-name header
     *
     * @param config Properties containing configuration information
     */
    protected void configRepositoryName(Properties config) {
        String name = config.getProperty("store.repository-name");
        if (name == null)
            throw new RuntimeException("Repository name not set in akubra-caringo configuration.");
        this.repositoryName = name;
    }

    /**
     * Configure the content router to enable BlobId enumeration. Optional - applies only if enough
     * appropriate configuration parameters are set.
     *
     * @param config Properties containing configuration information
     */
    protected void configContentRouter(Properties config) {
        String host = config.getProperty("content-router.host");
        String port = config.getProperty("content-router.port");
        String channel = this.configContentRouterChannel(config);
        if (host == null || port == null || channel == null) {
            this.contentRouterConfig = null;
        } else {
            this.contentRouterConfig = new FedoraContentRouterConfig(host, Integer.parseInt(port), channel);
        }
    }

    /**
     * Select the content router channel to be used to enumerate objects. Override in subclasses to have more
     * control over how this is done.
     *
     * @param config Properties containing configuration information
     * @return Name of content router channel
     */
    protected String configContentRouterChannel(Properties config) {
        return config.getProperty("content-router.channel");
    }

    /**
     * Add headers to storage object reflecting Fedora as the originator of the content.
     */
    protected void addOriginatorHeaders() {
        this.hints.addHint(":x-Dell-originator-meta", "Fedora");
        //attempt to extract fedora version and convert to Dell's desired format
        try {
            Class fedoraServer = Class.forName("org.fcrepo.server.Server");
            Field versionField = fedoraServer.getField("VERSION");
            String versionString = (String) versionField.get(null);
            this.hints.addHint(":x-Dell-originator-version-meta", versionString.replace('.', ';'));
        } catch (Exception e) {
            //for now we just pass here - do nothing if we can't detect the fedora version
        }
    }

    /**
     * Return a new connection to the blob store
     *
     * @param tx    Transaction. Must be null for this store.
     * @param hints Hints to initialize connection
     * @return New connection to blob store
     * @throws IOException If there is an error creating the connection
     */
    public FedoraBlobStoreConnection openConnection(Transaction tx, Map<String, String> hints) throws IOException {
        if (tx != null) {
            throw new UnsupportedOperationException();
        }
        return new FedoraBlobStoreConnection(this, streamManager, this.hints.copy_and_merge_hints(hints));
    }

    /**
     * Return a new connection to the blob store
     *
     * @return New connection to blob store
     * @throws IOException If there is a problem creating the connection
     */
    public FedoraBlobStoreConnection openConnection() throws IOException {
        return this.openConnection(null, null);
    }

    /**
     * Return the content router information
     *
     * @return ContentRouterConfig
     */
    protected FedoraContentRouterConfig getContentRouterConfig() {
        return this.contentRouterConfig;
    }

    /**
     * Return an Iterator over blobIds in this BlobStore.
     *
     * @param filterPrefix If not null then only return BlobIds starting with this string.
     * @return Iterator over blobIds in this BlobStore
     * @throws IOException
     */
    protected Iterator<URI> listBlobIds(String filterPrefix) throws IOException {
        try {
            return newBlobIterator(filterPrefix);
        } catch (ObjectEnumeratorException e) {
            throw new RuntimeException();
        } catch (ScspExecutionException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Return a new Iterator over the blobIds from this BlobStore
     *
     * @param filterPrefix
     * @return FedoraIterator
     * @throws IOException
     * @throws ObjectEnumeratorException
     * @throws ScspExecutionException
     */
    protected FedoraIterator newBlobIterator(String filterPrefix) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        return new FedoraIterator(this, filterPrefix);
    }

}
