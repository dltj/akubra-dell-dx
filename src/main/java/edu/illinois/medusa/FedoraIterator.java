package edu.illinois.medusa;

import com.caringo.client.ScspExecutionException;
import com.caringo.enumerator.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public abstract class FedoraIterator implements Iterator<URI> {

    protected EnumeratorResponse currentResponse;
    protected ObjectEnumerator enumerator;
    protected FedoraBlobStore blobStore;
    protected HashMap<String, String> queryArgs;

    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected FedoraIterator(FedoraBlobStore blobStore) throws IOException, ObjectEnumeratorException, ScspExecutionException {
        this.blobStore = blobStore;
        this.currentResponse = null;
        this.queryArgs = new HashMap<String, String>();
        FedoraContentRouterConfig contentRouterConfig = blobStore.getContentRouterConfig();
        this.enumerator = new ObjectEnumerator(contentRouterConfig.host, contentRouterConfig.port, EnumeratorType.ENUM_TYPE_METADATA);
        updateCurrentResponse();
    }

    protected void updateCurrentResponse() throws ObjectEnumeratorException, ScspExecutionException {
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
                if (acceptedResponse()) {
                    return;
                }
            }
        }
    }

    //Override in subclasses to reject currentResponse as part of the enumerator
    protected boolean acceptedResponse() {
        return true;
    }

    public boolean hasNext() {
        return this.currentResponse == null;
    }

    public URI next() throws ObjectEnumeratorException, ScspExecutionException {
        URI uri = extractURI();
        this.updateCurrentResponse();
        return uri;
    }

    protected URI extractURI() {
        //extract URI - maybe move this logic into accepted response (or call from there) and update a field
        //in the object at that point, since most likely we need to parse headers anyway to determine if
        //we want the object or not
    }
}
