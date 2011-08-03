package edu.illinois.medusa;

import com.google.common.primitives.Bytes;
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/18/11
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoMain {
    public static void main (String[] args) throws Exception {
        CaringoBlobStore store = new CaringoBlobStore(URI.create("caringo"), "cas.caringo.com", "uiuc", "cas.caringo.com");
        CaringoBlobStoreConnection connection = store.openConnection();
        CaringoBlob blob = connection.getBlob(URI.create("test"), null);

        /* output testing */
        /*
        OutputStream output = blob.openOutputStream(10, true);
        output.write("New test string 2".getBytes());
        connection.close();
        */

        System.out.println(blob.getSize());
        /*
        InputStream input = blob.openInputStream();
        InputStreamReader reader = new InputStreamReader(input);
        char[] chars = new char[128];
        int read = reader.read(chars);
        while (read != -1) {
            System.out.println("Batch:");
            System.out.println(chars);
            read = reader.read(chars);
        }
        */

        /*
        ByteArrayInputStream input = blob.openInputStream();
        byte[] bytes = new byte[512];
        int read = input.read(bytes);
        byte[] test_bytes = "Test string".getBytes();
        if (Arrays.equals(Arrays.copyOf(bytes, read), test_bytes)) {
            System.out.println("It worked!");
        }
        */

        /*delete testing*/
        /*
        blob.delete();
        connection.close();
        */
    }
}
