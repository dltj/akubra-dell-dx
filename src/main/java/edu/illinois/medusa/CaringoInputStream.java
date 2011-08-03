package edu.illinois.medusa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/20/11
 * Time: 10:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoInputStream extends FileInputStream {
    protected File file;

    protected CaringoInputStream(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    public void close() throws IOException {
        try {
            super.close();
        } finally {
            file.delete();
        }
    }

}
