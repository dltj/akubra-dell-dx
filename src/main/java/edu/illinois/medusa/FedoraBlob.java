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

    //We don't want to retain some of the headers on copy, specifically some that will already be present
    //or that should change for the new object
    @Override
    protected boolean copyableHeader(String header_name) {
        if (header_name == "x-fedora-meta-stream-id")
            return false;
        if (header_name == "x-fedora-meta-repository-name")
            return false;
        return super.copyableHeader(header_name);
    }
}
