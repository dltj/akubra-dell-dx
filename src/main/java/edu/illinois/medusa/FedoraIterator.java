package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.client.ScspHeader;
import com.caringo.enumerator.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Iterate over blobIds for a FedoraBlobStore. Uses the content router on the storage (via the CAStorSDK) to return an iterator going
 * over all blobIds. Handles filtering by prefix. Handles the possibility that the content router returns an Id more
 * than once. Subclasses may filter the blobs more finely, e.g. by returning only FOXML objects or managed datastreams.
 * <p/>
 * The basic idea is to obtain an iterator of the type provided by the CAStorSDK and to implement the methods of this
 * iterator using that and some filtering.
 *
 * @author Howard Ding - hding2@illinois.edu
 */

public class FedoraIterator implements Iterator<URI> {

    /**
     * BlobStore over which this iterator iterates
     */
    protected FedoraBlobStore blobStore;
    /**
     * If non null the iterator will only return blobs starting with this prefix
     */
    protected String filterPrefix;

    /**
     * Underlying CAStorSDK enumerator on the configured channel
     */
    protected ObjectEnumerator enumerator;
    /**
     * Most recent response from enumerator
     */
    protected EnumeratorResponse currentResponse;
    /**
     * Most recent headers from enumerator
     */
    protected ArrayList<ScspHeader> currentHeaders;
    /**
     * Most recent PID (x-fedora-meta-stream-id) from enumerator
     */
    protected String currentPID;

    /**
     * Query args to send with enumerator request
     */
    protected HashMap<String, String> queryArgs;

    /**
     * Set of PIDs already reported by enumerator
     */
    protected HashSet<String> seenPIDs;

    /**
     * Construct a new iterator over the blobIds in a BlobStore
     *
     * @param blobStore    FedoraBlobStore over which to iterate
     * @param filterPrefix If not null then only return blobIds starting with this prefix
     * @throws IOException
     * @throws ObjectEnumeratorException
     * @throws ScspExecutionException
     */
    protected FedoraIterator(FedoraBlobStore blobStore, String filterPrefix) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        //Store initialization data
        this.blobStore = blobStore;
        this.filterPrefix = filterPrefix;
        //Initialize variables used to track iteration
        this.currentResponse = null;
        this.queryArgs = new HashMap<String, String>();
        this.seenPIDs = new HashSet<String>();
        //Create and start underlying enumerator
        FedoraContentRouterConfig contentRouterConfig = blobStore.getContentRouterConfig();
        this.enumerator = new ObjectEnumerator(contentRouterConfig.host, contentRouterConfig.port, EnumeratorType.ENUM_TYPE_METADATA);
        this.enumerator.start(contentRouterConfig.channel);
        currentHeaders = new ArrayList<ScspHeader>();
        //Get first object
        updateCurrentResponse();
    }

    /**
     * Unsupported - this iterator does not allow removal of objects
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Answer whether there is another element to be iterated over
     *
     * @return Whether there is another element
     */
    public boolean hasNext() {
        if (currentResponse == null) {
            stopEnumerator();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Stop the underlying CAStorSDK enumerator
     */
    public void stopEnumerator() {
        try {
            if (enumerator != null) {
                enumerator.end();
            }
        } catch (Exception e) {
            //do nothing
        } finally {
            enumerator = null;
        }
    }

    /**
     * Cleanup actions
     */
    protected void finalize() {
        stopEnumerator();
    }

    /**
     * Return the next element in the iteration
     *
     * @return Next element in the iteration
     */
    public URI next() {
        //We save the currentPID for returning and update to the next object internally.
        URI uri = URI.create(currentPID);
        try {
            this.updateCurrentResponse();
            return uri;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * Get the next applicable element from the underlying CAStor iterator and update this iterator's internal state.
     *
     * @throws IOException
     * @throws ObjectEnumeratorException
     * @throws ScspExecutionException
     */
    protected void updateCurrentResponse() throws IOException, ObjectEnumeratorException, ScspExecutionException {
        EnumeratorResponse response;
        ArrayList<EnumeratorEntry> entries;
        while (true) {
            response = enumerator.next(1L, queryArgs);
            entries = response.getEntries();
            if (entries == null) {
                //TODO still confirming with Dell, but I think null means that the content router doesn't have anything
                //to return at time of request, but it's also not saying that the enumeration is done, So the client
                //should wait and try again.
                throw new RuntimeException("Null entries received by metadata enumerator.");
            } else if (entries.size() == 0) {
                currentResponse = null;
                return;
            } else {
                currentResponse = response;
                parseCurrentResponse();
                if (acceptResponse())
                    return;
            }
        }
    }

    /**
     * Extract and store information from last response and headers from underlying CAStorSDK iterator.
     */
    protected void parseCurrentResponse() {
        parseCurrentHeaders();
        parseCurrentPID();
    }

    //Hopefully at some point this will just be currentResponse.getEntries.get(0).getScspHeaders() but that
    //doesn't work correctly right now. My parsing here will also fail if there are header values that span
    //multiple lines, but I don't think we'll run into that. If so we can take that into account or find
    //an existing library that parses a string representing http headers.

    /**
     * Extract relevant information from response body of CAStorSDK metadata enumerator request response.
     * See comment above for more about why we do it this way - there's a small bug in the SDK.
     */
    protected void parseCurrentHeaders() {
        String responseBody = currentResponse.getResponseBody();
        String[] lines = responseBody.split("\r?\n|\r");
        currentHeaders.clear();
        for (String line : lines) {
            int index = line.indexOf(':');
            if (index >= 0) {
                ScspHeader header = new ScspHeader(line.substring(0, index), line.substring(index + 1).trim());
                currentHeaders.add(header);
            }
        }
    }

    /**
     * The id of the blob, as passed from Fedora to Akubra, is stored in the x-fedora-meta-stream-id header, so
     * all we need to do is to extract that directly.
     */
    protected void parseCurrentPID() {
        currentPID = getCurrentHeaderValue("x-fedora-meta-stream-id");
    }

    /**
     * Determine if an element returned by the underlying CAStorSDK iterator is appropriate for return from
     * this iterator. Override (and super call) in subclasses to add more conditions. This checks that
     * - the prefix condition, if used, is met
     * - the PID hasn't been seen before
     * - an object with the PID actually exists in storage
     *
     * @return Whether the current response should be included in this iterator
     * @throws IOException
     */
    protected boolean acceptResponse() throws IOException {
        return prefixCheck() && previouslyUnseenPID() && existenceCheck();
    }

    /**
     * Check the current response against the filter prefix condition.
     *
     * @return Whether the current response meets the filter prefix condition.
     */
    protected boolean prefixCheck() {
        if (filterPrefix == null)
            return true;
        return currentPID.startsWith(filterPrefix);
    }

    /**
     * Check to see if the current PID has been seen before. If not then record it.
     * Note that the current implementation uses an in-memory set to keep track of this. Should the
     * sets of objects become too large some early experimentation indicates that BerkeleyDB should
     * be able to solve the problem of keeping track of seen PIDs in an efficient manner.
     *
     * @return Whether the current PID has been seen before
     */
    protected boolean previouslyUnseenPID() {
        if (seenPIDs.contains(currentPID)) {
            return false;
        } else {
            seenPIDs.add(currentPID);
            return true;
        }
    }

    //Require the blob to actually exist in storage
    //There is some chance that the call to check here will throw
    //an IOException because of an unexpected 500 error from storage.
    //There is a little bit of a retry facility in the exists() call
    //itself, but (imagine we're doing a rebuild on a repo with millions
    //of objects) we may want to augment that here to make really sure
    //that we don't die unnecessarily.

    /**
     * Check whether a blob with the current PID exists in storage. Note that because of the way the content
     * router works it is possible for the underlying enumerator to return PIDs that have previously been
     * deleted, although it is by no means clear when it does and does not.
     *
     * @return Whether an object with the current PID exists in the storage unit.
     * @throws IOException
     */
    protected boolean existenceCheck() throws IOException {
        FedoraBlob blob = blobStore.openConnection().getBlob(URI.create(currentPID), null);
        return blob.exists();
    }

    /**
     * Return the value of the first header equal to key.
     * For now we don't need to worry about multiple headers with the same name.
     *
     * @param key Header name
     * @return Header value corresponding to key.
     */
//Note that currently this only finds the first matching header. That's all we need for now.
    protected String getCurrentHeaderValue(String key) {
        if (currentHeaders == null)
            return null;
        for (ScspHeader header : currentHeaders) {
            if (key.equals(header.getName()))
                return header.getValue();
        }
        return null;
    }

}