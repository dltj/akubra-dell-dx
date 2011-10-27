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

    /*
    TODO
    I wonder if we could do better on this. As is, it pulls the entire stream from Caringo, writes
    to a temporary file, and the provided input stream is an InputStream on that file.
    One possibility (depending on what the requirements of this InputStream are) might be to
    use the ability to fetch pieces of a datastream (does the CaringoSDK support this) and expose
    them via a circular buffer or piped input stream. So maybe create a piped input stream, hook it
    to a piped output stream. Spin off a thread that does an info on the object to get the size then
    reads it piece by piece and writes to the pipe. Overall this might be less efficient, but for
    large files data would start to appear earlier.

    Probably the corresponding idea for writing to Caringo is less important, since there's not really
    any possible visible result until the entire operation is complete.
     */
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
        //File tempFile = File.createTempFile(this.getId().toString(), ".blob");
        File tempFile = File.createTempFile("fedora-out", ".blob");
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
