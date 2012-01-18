package edu.illinois.medusa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Used to return objects from Caringo storage for reading by clients.
 *
 * As currently implemented it writes the bytes from Caringo storage into a temporary file and gives the client
 * an InputStream on that file. When closed it removes the file.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class CaringoInputStream extends FileInputStream {

    /**
     * Temporary file to hold bytes received from Caringo
     */
    protected File file;

    /**
     * Construct given a temporary file.
     *
     * @param file The temporary file in which to hold the bytes received from Caringo
     * @throws FileNotFoundException If a FileInputStream cannot be constructed on file
     */
    protected CaringoInputStream(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    /**
     * Close the stream and delete the underlying file.
     *
     * @throws IOException If there is a problem closing the underlying file
     */
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            file.delete();
        }
    }

}
