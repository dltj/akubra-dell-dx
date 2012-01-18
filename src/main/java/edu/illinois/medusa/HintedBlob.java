package edu.illinois.medusa;


import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Blob that can use Akubra hints to make/copy Caringo metadata headers
 *
 * This class doesn't add any headers, but will copy x-*-meta-* headers and lifepoint headers.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class HintedBlob extends CaringoBlob {

    /**
     * Hints used to generate Caringo headers
     */
    protected CaringoHints hints;
    /**
     * Connection that this blob uses to communicate with Caringo storage
     */
    protected HintedBlobStoreConnection owner;
    /**
     * In charge of how to copy hints from this blob to another
     */
    protected HintCopier hintCopier;
    /**
     * List of hints to add to this kind of blob before storing - subclasses can add to this to configure
     */
    protected List<HintAdder> hintAdders;
    /**
     * Stream holding content to be written to storage by this blob - used before writing
     */
    protected CaringoOutputStream content;

    /**
     * Construct a new HintedBlob
     *
     * @param owner Owning connection for this blob
     * @param id ID of the blob
     * @param hints Any initializing hints for this blob
     */
    protected HintedBlob(HintedBlobStoreConnection owner, URI id, CaringoHints hints) {
        super(owner, id);
        this.hints = hints;
        this.owner = owner;
        this.hintCopier = new HintCopier();
        this.hintCopier.addRule(new HintCopyRegexpRule("caringo-meta", true, "^x-.+-meta-.+$"));
        this.hintCopier.addRule(new HintCopyRegexpRule("caringo-lifepoint", true, "^Lifepoint$"));
        this.hintAdders = new ArrayList<HintAdder>();
    }

    /**
     * Copy appropriate headers from this blob to another. Note that we need to have done an info or read request
     * with this blob to have access to the relevant headers
     *
     * @param otherBlob Blob to which to copy headers
     */
    protected void copyHeaders(HintedBlob otherBlob) {
        this.hintCopier.copyHeaders(this, otherBlob);
    }

    /**
     * Return md5 checksum for the content of this blob. Only applies before a write operation, i.e. a client
     * has obtained an output stream on the blob, written to it, and closed it.
     * @return MD% checksum as byte array - note that this is how the Caringo storage wants it
     */
    protected byte[] md5checksum() {
     try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return content.md5Sum();
        } catch (Exception e) {
            throw new RuntimeException("MD5 Message Digest not found");
        }
    }

    /**
     * Add a hint for this blob
     *
     * @param key Key for hint
     * @param value Value of hint
     */
    public void addHint(String key, String value) {
        this.hints.addHint(key, value);
    }

    /**
     * Send a write request to the Caringo server. Used after closing an output stream on this blob.
     * Overriding here is necessary to incorporate the hints into headers.
     *
     * @param content OutputStream to be written
     * @param overwrite Whether or not it is permissible to overwrite this blob if it is already in storage
     * @return Wrapped response from server
     * @throws IOException If there is a problem writing to Caringo storage
     */
    protected CaringoWriteResponse sendWrite(CaringoOutputStream content, boolean overwrite) throws IOException {
        return this.owner.write(this.id, content, overwrite, this.hints);
    }

    /**
     * Action to take before writing bytes to Caringo storage
     * In this case set the content stream so it will be available elsewhere and add Hints as appropriate.
     * @param content The OutputStream with the bytes to be written
     */
    protected void preprocessWrite(CaringoOutputStream content) {
        this.content = content;
        for (HintAdder hintAdder : this.hintAdders) {
            hintAdder.addHints(this);
        }
    }

    /**
     * Action to take after successfully writing bytes to Caringo storage.
     * In this case just clear the content variable
     */
    protected void postprocessWrite() {
        this.content = null;
    }

    /**
     * Action to take before moving content from this blob to a target.
     * In this case just copy headers as directed by the HintCopier.
     *
     * @param newBlob The blob being moved to.
     */
    protected void preprocessMoveTo(CaringoBlob newBlob) {
        this.copyHeaders((HintedBlob) newBlob);
    }

}
