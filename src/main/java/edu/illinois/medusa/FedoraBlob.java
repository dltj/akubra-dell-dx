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
        this.hintCopier.addRuleFront(new HintCopyRegexpRule("reject-stream-id", false, "^x-fedora-meta-stream-id$"));
        this.hintCopier.addRuleFront(new HintCopyRegexpRule("reject-stream-id", false, "^x-fedora-meta-repository-name$"));
        this.hintAdders.add(new HintIdAdder());
        this.hintAdders.add(new HintMD5Adder());
    }

}
