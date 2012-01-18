package edu.illinois.medusa;

/**
 * Add blob id as a Caringo header
 *
 * @author Howard Ding - hding2@illinois.edu
 */

public class HintIdAdder extends HintAdder {

    /**
     * key mapping to header under which to add the blob id
     */
    protected String key;

    /**
     * Construct a hint adder for the blob id using the given key to make the header
     *
     * @param key Key mapping to the header to be used for the id
     */
    protected HintIdAdder(String key) {
        this.key = key;
    }

    /**
     * Add this hint to a blob
     *
     * @param blob Blob to receive the hint
     */
    public void addHints(HintedBlob blob) {
        blob.addHint(this.key, blob.getId().toString());
    }
}
