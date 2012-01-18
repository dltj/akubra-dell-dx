package edu.illinois.medusa;

import com.caringo.client.ScspResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

/**
 * Wrapper for response from read request to Caringo storage. Includes the temp file created to hold the data.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class CaringoReadResponse extends CaringoAbstractResponse {

    /**
     * File that holds the bytes received from storage
     */
    protected File file;

    /**
     * The File holding the bytes read from storage.
     * @return File holding bytes read from storage
     */
    public File getFile() {
        return file;
    }

    /**
     * Construct this object, wrapping the Caringo response and file holding the content bytes of the response
     *
     * @param response ScspResponse returned by Caringo
     * @param file File holding content of object that was read
     */
    protected CaringoReadResponse(ScspResponse response, File file) {
        super(response);
        this.file = file;
    }

    /**
     * Remove the file holding the bytes retrieved from storage
     */
    public void cleanupFile() {
        file.delete();
    }
}
