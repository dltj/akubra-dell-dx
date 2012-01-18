package edu.illinois.medusa;

import com.caringo.client.ResettableFileInputStream;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * OutputStream for writing to Caringo storage
 *
 * An Akubra Blob can get one of these. It creates a temporary file to which the bytes are written. When this
 * stream is closed it writes its information to Caringo.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class CaringoOutputStream extends FileOutputStream {

    /**
     * Whether this is allowed to overwrite an existing object.
     */
    protected boolean overwrite;
    /**
     * Blob that requested this output stream
     */
    protected CaringoBlob blob;
    /**
     * Local temp file backing this output stream
     */
    protected File file;

    /**
     * Construct a new CaringoOutputStream
     *
     * @param estimated_length Estimated length of this output stream - ignored
     * @param overwrite Whether or not this is allowed to overwrite an existing object
     * @param blob Blob that requested this output stream
     * @param tempFile Temporary file that will back this output stream
     * @throws FileNotFoundException If a FileStream can't be opened on tempFile
     */
    protected CaringoOutputStream(long estimated_length, boolean overwrite,
                                  CaringoBlob blob, File tempFile) throws FileNotFoundException {
        super(tempFile);
        this.overwrite = overwrite;
        this.blob = blob;
        this.file = tempFile;
    }

    /**
     * An MD5 sum for the bytes in this stream
     *
     * @return Byte array giving the MD5 sum for the bytes in the stream
     */
    //TODO this is obviously inefficient. We ought to be able to do something similar on the output stream while
    //writing the file, but this is simpler to get something working, so I'm going with it for now.
    public byte[] md5Sum() {
        try {
            FileInputStream fileStream = new FileInputStream(this.file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            DigestInputStream digestStream = new DigestInputStream(fileStream, md);
            byte[] bytes = new byte[4096];
            while (digestStream.read(bytes) != -1) {}
            return digestStream.getMessageDigest().digest();
        } catch (Exception e) {
            throw new RuntimeException("Problem computing MD5 sum for blob");
        }
    }

    /**
     * Close this stream, removing the backing temp file.
     *
     * @throws IOException If there is a problem closing the file
     */
    public void close() throws IOException {
        //try to write the contentStream
        try {
            blob.write(this, this.overwrite);
        } finally {
            try {
                super.close();
            } finally {
                file.delete();
            }
        }
    }

    /**
     * Number of bytes in the stream
     *
     * @return Number of bytes in the stream
     */
    public long size() {
        return file.length();
    }

    /**
     * Return a new InputStream with the contents of this output stream. Used after accumulating the bytes from the
     * client when writing the bytes to caringo
     *
     * @return An InputStream with the contents of this stream
     * @throws IOException If there is a problem constructing the InputStream
     */
    public InputStream contentStream() throws IOException {
        return new ResettableFileInputStream(file);
    }
}
