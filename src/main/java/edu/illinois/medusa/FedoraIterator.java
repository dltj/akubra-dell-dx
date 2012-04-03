package edu.illinois.medusa;

import java.net.URI;
import java.util.Iterator;

public abstract class FedoraIterator implements Iterator<URI> {
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
