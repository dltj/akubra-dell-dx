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

    protected HintedBlob(HintedBlobStoreConnection owner, URI id, CaringoHints hints) {
        super(owner, id);
        this.hints = hints;
        this.owner = owner;
    }

    //copy selected headers from this to otherBlob
    //Note that we must have done an info or opened an Input stream on this so as to have access to
    //a response with headers
    protected void copyHeaders(HintedBlob otherBlob) {
        if (this.response != null) {
            ScspHeaders headers = this.response().scspResponse().getResponseHeaders();
            HashMap<String, ArrayList<String>> header_map = headers.getHeaderMap();
            for(String key : header_map.keySet()) {
                if (this.copyableHeader(key)) {
                    for(String value : header_map.get(key)) {
                        otherBlob.addHint(":" + key, value);
                    }
                }
            }
        }
    }

    //return whether a header with the given name should be copied when doing moveTo
    //We copy x-*-meta-* headers and Lifepoint headers. Of course one can subclass to change this.
    protected boolean copyableHeader(String header_name) {
        if (header_name.matches("^x-.+-meta-.+$"))
            return true;
        if (header_name.matches("^Lifepoint$"))
            return true;
        return false;
    }

    public void addHint(String key, String value) {
        this.hints.addHint(key, value);
    }

    protected void write(CaringoOutputStream content, boolean overwrite) throws IOException, DuplicateBlobException {
        if (!overwrite && this.exists()) {
            throw new DuplicateBlobException(this.id);
        }
        CaringoWriteResponse writeResponse = this.owner.write(this.id, content, overwrite, this.hints);
        response = writeResponse;
        if (!writeResponse.created())
            throw new IOException();
    }

    public Blob moveTo(URI uri, Map<String, String> stringStringMap) throws DuplicateBlobException, IOException, MissingBlobException, NullPointerException, IllegalArgumentException {
        if (!this.exists())
            throw new MissingBlobException(this.id);
        if (uri == null)
            throw new UnsupportedOperationException();
        HintedBlob newBlob = this.owner.getBlob(uri, null);
        if (newBlob.exists())
            throw new DuplicateBlobException(uri);

        //store content in new blob
        InputStream input = this.openInputStream();
        this.copyHeaders(newBlob);
        OutputStream output = newBlob.openOutputStream(1024, false);
        IOUtils.copyLarge(input, output);
        output.close();
        input.close();

        //remove old blob
        this.delete();

        return newBlob;
    }

}
