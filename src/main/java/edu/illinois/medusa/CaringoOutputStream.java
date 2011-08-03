package edu.illinois.medusa;

import com.caringo.client.ResettableFileInputStream;

import java.io.*;

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
