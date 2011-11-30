package edu.illinois.medusa;

import com.caringo.client.ResettableFileInputStream;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/18/11
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoOutputStream extends FileOutputStream {

    protected boolean overwrite;
    protected CaringoBlob blob;
    protected File file;

    //
    protected CaringoOutputStream(long estimated_length, boolean overwrite,
                                  CaringoBlob blob, File tempFile) throws FileNotFoundException {
        super(tempFile);
        this.overwrite = overwrite;
        this.blob = blob;
        this.file = tempFile;
    }

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

    public long size() {
        return file.length();
    }

    public InputStream contentStream() throws IOException {
        return new ResettableFileInputStream(file);
    }
}
