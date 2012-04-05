package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.client.ScspHeader;
import com.caringo.enumerator.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public abstract class FedoraIterator implements Iterator<URI> {

    protected EnumeratorResponse currentResponse;
    protected ArrayList<ScspHeader> currentHeaders;
    protected String currentPID;
    protected String currentStreamID;
    protected ObjectEnumerator enumerator;
    protected FedoraBlobStore blobStore;
    protected HashMap<String, String> queryArgs;

    protected FedoraIterator(FedoraBlobStore blobStore) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        this.blobStore = blobStore;
        this.currentResponse = null;
        this.queryArgs = new HashMap<String, String>();
        FedoraContentRouterConfig contentRouterConfig = blobStore.getContentRouterConfig();
        this.enumerator = new ObjectEnumerator(contentRouterConfig.host, contentRouterConfig.port, EnumeratorType.ENUM_TYPE_METADATA);
        currentHeaders = new ArrayList<ScspHeader>();
        updateCurrentResponse();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        return this.currentResponse != null;
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
            if (entries.size() == 0) {
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

    protected void parseCurrentPID() {
        currentStreamID = getCurrentHeaderValue("x-fedora-meta-stream-id");
        if (currentStreamID == null)
            currentPID = null;
        else
            currentPID = currentStreamID.split("/")[1];
    }

    //Override in subclasses to add conditions for rejection
    //Currently we check to make sure that the object actually exists in storage
    protected boolean acceptResponse() throws IOException {
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