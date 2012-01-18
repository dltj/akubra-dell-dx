package edu.illinois.medusa;

import org.apache.commons.codec.binary.Base64;

/**
 * Add an MD5 checksum header when writing an object
 *
 * @author Howard Ding - hding2@illinois.edu
 */

public class HintMD5Adder extends HintAdder {

    /**
     * Add a hint for the md5 checksum to a blob
     *
     * @param blob Blob receiving the MD5 header
     */
    public void addHints(HintedBlob blob) {
        blob.addHint(":Content-MD5", base64MD5checksum(blob));
    }

    /**
     * Produce a base64 encoded md5 checksum for blob contents
     *
     * @param blob Blob to be checksummed
     * @return Base64 encoding of md5 checksum (computed as array of bytes)
     */
    private String base64MD5checksum(HintedBlob blob) {
        return new String(Base64.encodeBase64(md5checksum(blob)));
    }

    /**
     * Produce an md5 checksum for the blob as an array of bytes
     *
     * @param blob Blob to be checksummed
     * @return MD5 checksum as array of bytes
     */
    private byte[] md5checksum(HintedBlob blob) {
        return blob.md5checksum();
    }
}
