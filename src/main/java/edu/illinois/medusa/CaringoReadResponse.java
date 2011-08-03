package edu.illinois.medusa;

import com.caringo.client.ScspResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/18/11
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoReadResponse extends CaringoAbstractResponse {

    public File getFile() {
        return file;
    }

    protected File file;


    protected CaringoReadResponse(ScspResponse response, File file) {
        super(response);
        this.file = file;
    }

    public void cleanupFile() {
        file.delete();
    }
}
