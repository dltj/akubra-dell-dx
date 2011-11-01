package edu.illinois.medusa;

import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/1/11
 * Time: 10:45 AM
 */
public class FedoraBlob extends HintedBlob {

    protected FedoraBlob(FedoraBlobStoreConnection owner, URI id, CaringoHints hints) {
        super(owner, id, hints);
        this.addHint("fedora:stream-id", id.toString());
    }
}
