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

public class FedoraIterator implements Iterator<URI> {

    protected EnumeratorResponse currentResponse;
    protected ArrayList<ScspHeader> currentHeaders;
    protected String currentPID;
    protected ObjectEnumerator enumerator;
    protected FedoraBlobStore blobStore;
    protected HashMap<String, String> queryArgs;
    protected String filterPrefix;
    protected HashSet<String> seenPIDs;

    protected FedoraIterator(FedoraBlobStore blobStore, String filterPrefix) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        this.blobStore = blobStore;
        this.filterPrefix = filterPrefix;
        this.currentResponse = null;
        this.queryArgs = new HashMap<String, String>();
        this.seenPIDs = new HashSet<String>();
        FedoraContentRouterConfig contentRouterConfig = blobStore.getContentRouterConfig();
        this.enumerator = new ObjectEnumerator(contentRouterConfig.host, contentRouterConfig.port, EnumeratorType.ENUM_TYPE_METADATA);
        this.enumerator.start(contentRouterConfig.channel);
        currentHeaders = new ArrayList<ScspHeader>();
        updateCurrentResponse();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        if (currentResponse == null) {
            stopEnumerator();
            return false;
        } else {
            return true;
        }
    }

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

    protected void finalize() {
        stopEnumerator();
    }

    public URI next() {
        URI uri = URI.create(currentPID);
        try {
            this.updateCurrentResponse();
            return uri;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

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

    //extract and store headers from current response and URI
    protected void parseCurrentResponse() {
        parseCurrentHeaders();
        parseCurrentPID();
    }

    //Hopefully at some point this will just be currentResponse.getEntries.get(0).getScspHeaders() but that
    //doesn't work correctly right now. My parsing here will also fail if there are header values that span
    //multiple lines, but I don't think we'll run into that. If so we can take that into account or find
    //an existing library that parses a string representing http headers.
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

    //note that, per HintIdAdder, the blob id itself is used as x-fedora-meta-stream-id, so that is what we should
    //use here
    protected void parseCurrentPID() {
        currentPID = getCurrentHeaderValue("x-fedora-meta-stream-id");
    }

    //Override in subclasses to add conditions for rejection
    //We break out each check into a separate method for simplicity
    protected boolean acceptResponse() throws IOException {
        return prefixCheck() && previouslyUnseenPID() && existenceCheck();
    }

    //If filterPrefix is set then require the currentPID to start with it; if not always accept
    protected boolean prefixCheck() {
        if (filterPrefix == null)
            return true;
        return currentPID.startsWith(filterPrefix);
    }

    //If we've seen this pid before return false
    //If not then store it and return true
    //Note that currently we track this in memory, but if that becomes a concern a simple embedded database
    //would be useable (e.g. Berkeley DB, sqlite, derby). Just create along with enumerator and store pids
    //in a table with index.
    protected boolean previouslyUnseenPID() {
        if (seenPIDs.contains(currentPID)) {
            return false;
        } else {
            seenPIDs.add(currentPID);
            return true;
        }
    }

    //Require the blob to actually exist in storage
    protected boolean existenceCheck() throws IOException {
        FedoraBlob blob = blobStore.openConnection().getBlob(URI.create(currentPID), null);
        return blob.exists();
    }

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