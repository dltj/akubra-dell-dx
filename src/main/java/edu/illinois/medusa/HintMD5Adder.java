package edu.illinois.medusa;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/30/11
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class HintMD5Adder extends HintAdder {
    public void addHints(HintedBlob blob) {
        blob.addHint(":Content-MD5", base64MD5checksum(blob));
    }

    private String base64MD5checksum(HintedBlob blob) {
        return new String(Base64.encodeBase64(md5checksum(blob)));
    }

    private byte[] md5checksum(HintedBlob blob) {
        return blob.md5checksum();
    }
}
