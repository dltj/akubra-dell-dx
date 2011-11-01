package edu.illinois.medusa;

import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/1/11
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraBlob extends CaringoBlob {

    protected FedoraBlob(CaringoBlobStoreConnection owner, URI id, CaringoHints hints) {
        super(owner, id, hints);
        this.addHint("fedora:stream-id", id.toString());
        this.addHint("fedora:stream", "true");
    }
}
