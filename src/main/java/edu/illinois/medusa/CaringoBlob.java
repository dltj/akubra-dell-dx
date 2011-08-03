package edu.illinois.medusa;

import org.akubraproject.Blob;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.MissingBlobException;
import org.akubraproject.impl.AbstractBlob;
import org.akubraproject.impl.StreamManager;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/14/11
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoBlob extends AbstractBlob {

    protected CaringoBlobStoreConnection owner;

    protected CaringoBlob(CaringoBlobStoreConnection owner, URI id) {
        super(owner, id);
        this.owner = owner;
    }

    protected StreamManager getStreamManager() {
        return this.owner.getStreamManager();
    }

    @Override
    public boolean exists() throws IOException {
        CaringoInfoResponse response = this.info();
        if (response.notFound())
            return false;
        if (response.ok())
            return true;
        throw new IOException();
    }

    @Override
    public long getSize() throws IOException, MissingBlobException {
        CaringoInfoResponse response = this.info();
        if (response.notFound()) {
            throw new MissingBlobException(this.id);
        }
        if (!response.ok()) {
            throw new IOException();
        }
        return response.contentLength();
    }

    private CaringoInfoResponse info() throws IOException {
        return this.owner.info(this.id);
    }

    @Override
    public InputStream openInputStream() throws IOException, MissingBlobException {
        CaringoReadResponse response = this.owner.read(this.id);
        if (response.notFound()) {
            response.cleanupFile();
            throw new MissingBlobException(this.id);
        }

        if (!response.ok()) {
            response.cleanupFile();
            throw new IOException();
        }
        CaringoInputStream input = new CaringoInputStream(response.getFile());
        return this.getStreamManager().manageInputStream(this.owner, new BufferedInputStream(input));
    }

    @Override
    public OutputStream openOutputStream(long estimated_length, boolean overwrite) throws IOException, DuplicateBlobException {
        File tempFile = File.createTempFile(this.getId().toString(), ".blob");
        //Just to make sure that the file is cleaned up - however, we do it manually when the stream opened on it
        //is closed. Hopefully this doesn't create problems, i.e. deleteFileOnExit is still okay if the file
        //is already gone.
        tempFile.deleteOnExit();
        CaringoOutputStream outputStream = new CaringoOutputStream(estimated_length, overwrite, this, tempFile);
        return this.getStreamManager().manageOutputStream(this.owner, new BufferedOutputStream(outputStream));
    }

    @Override
    public void delete() throws IOException {
        CaringoDeleteResponse response = this.owner.delete(this.id);
        if (!response.ok() && !response.notFound())
            throw new IOException();
    }

    //TODO
    //The SDK has a tantalizingly named copyMutable method and the examples seem to indicate that it can
    //be applied to objects in buckets, but I'm not sure it does what we want. In fact, it's not completely
    //clear to me what it does at all. But it might be worth figuring out just in case I'm missing something
    //here, as making this round trip to copy isn't exactly ideal.
    @Override
    public Blob moveTo(URI uri, Map<String, String> stringStringMap) throws DuplicateBlobException, IOException, MissingBlobException, NullPointerException, IllegalArgumentException {
        if (!this.exists())
            throw new MissingBlobException(this.id);
        if (uri == null)
            throw new UnsupportedOperationException();
        Blob newBlob = this.owner.getBlob(uri, null);
        if (newBlob.exists())
            throw new DuplicateBlobException(uri);

        //store content in new blob
        OutputStream output = newBlob.openOutputStream(1024, false);
        InputStream input = this.openInputStream();
        IOUtils.copyLarge(input, output);
        output.close();
        input.close();

        //remove old blob
        this.delete();

        return newBlob;
    }

    protected void write(CaringoOutputStream content, boolean overwrite) throws IOException, DuplicateBlobException {
        if (!overwrite && this.exists()) {
            throw new DuplicateBlobException(this.id);
        }
        CaringoWriteResponse response = this.owner.write(this.id, content, overwrite);
        if (!response.created())
            throw new IOException();
    }

}
