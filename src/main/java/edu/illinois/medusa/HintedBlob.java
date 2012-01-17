package edu.illinois.medusa;

import com.caringo.client.ScspHeaders;
import org.akubraproject.Blob;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.MissingBlobException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/1/11
 * Time: 12:27 PM
 */
public class HintedBlob extends CaringoBlob {

    protected CaringoHints hints;
    protected HintedBlobStoreConnection owner;
    protected HintCopier hintCopier;
    protected List<HintAdder> hintAdders;
    protected CaringoOutputStream content;

    protected HintedBlob(HintedBlobStoreConnection owner, URI id, CaringoHints hints) {
        super(owner, id);
        this.hints = hints;
        this.owner = owner;
        this.hintCopier = new HintCopier();
        this.hintCopier.addRule(new HintCopyRegexpRule("caringo-meta", true, "^x-.+-meta-.+$"));
        this.hintCopier.addRule(new HintCopyRegexpRule("caringo-lifepoint", true, "^Lifepoint$"));
        this.hintAdders = new ArrayList<HintAdder>();
    }

    //copy selected headers from this to otherBlob
    //Note that we must have done an info or opened an Input stream on this so as to have access to
    //a response with headers
    protected void copyHeaders(HintedBlob otherBlob) {
        this.hintCopier.copyHeaders(this, otherBlob);
    }

    public CaringoOutputStream getContent() {
        return this.content;
    }

    public void addHint(String key, String value) {
        this.hints.addHint(key, value);
    }

    protected CaringoWriteResponse sendWrite(CaringoOutputStream content, boolean overwrite) throws IOException {
        return this.owner.write(this.id, content, overwrite, this.hints);
    }

    protected void preprocessWrite(CaringoOutputStream content) {
        this.content = content;
        for (HintAdder hintAdder : this.hintAdders) {
            hintAdder.addHints(this);
        }
    }

    protected void postprocessWrite() {
        this.content = null;
    }

    protected void preprocessMoveTo(CaringoBlob newBlob) {
        this.copyHeaders((HintedBlob) newBlob);
    }

}
