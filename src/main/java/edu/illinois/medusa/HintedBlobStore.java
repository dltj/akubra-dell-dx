package edu.illinois.medusa;

import javax.transaction.Transaction;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Blob Store that can use Akubra hints to maintain Caringo headers
 *
 * @author Howard Ding - hding2@illinois.edu
 */

public class HintedBlobStore extends CaringoBlobStore {

    /**
     * Hints to be used over the whole store
     */
    protected CaringoHints hints;

    protected HintedBlobStore(URI storeId, String configFilePath) {
        super(storeId, configFilePath);
        this.addCreatorHeaders();
    }

    /**
     * Open a connection to Caringo storage
     *
     * @param tx    For this BlobStore this must be null - transactions are unsupported.
     * @param hints Any hints to initialize this connection
     * @return new connection
     * @throws IOException If there is any problem opening the connection or tx is not null.
     */
    public HintedBlobStoreConnection openConnection(Transaction tx, Map<String, String> hints) throws IOException {
        if (tx != null) {
            throw new UnsupportedOperationException();
        }
        return new HintedBlobStoreConnection(this, streamManager, this.hints.copy_and_merge_hints(hints));
    }

    /**
     * Open a connection to Caringo storage
     *
     * @return new connection
     * @throws IOException If there is any problem opening the connection
     */
    public HintedBlobStoreConnection openConnection() throws IOException {
        return this.openConnection(null, null);
    }

    //currently this doesn't do anything, but I'm putting it here in preparation for being able to convert
    //header.x = y|z properties into headers.
    //This would involve:
    //Extract those keys (header.<header-name>)
    //Parse keys and values
    //Add to hints
    protected void configFromProperties(Properties config) {
        this.hints = new CaringoHints();
        super.configFromProperties(config);
        this.addConfigHeaders(config);
    }

    protected void addConfigHeaders(Properties config) {
        Set<String> keys = config.stringPropertyNames();
        for (String key : keys) {
            if (key.startsWith("header.")) {
                String headerName = key.substring("header.".length());
                List<String> values = parseConfigHeaders(config.getProperty(key));
                for (String value : values) {
                    this.hints.addHint(":" + headerName, value);
                }
            }
        }
    }

    //Add some headers related to this plugin as requested by Dell. I don't think there is any problem
    //adding them from a raw Caringo perspective - at worst perhaps they'll be ignored.
    //Note that there is an analogous method in FedoraBlobStore for adding originator information -
    //I don't want to make the assumption that Fedora is the client until that point.
    protected void addCreatorHeaders() {
        this.hints.addHint(":x-Dell-creator-meta", AkubraPlugin.dellCreator);
        this.hints.addHint(":x-Dell-creator-version-meta", AkubraPlugin.dellCreatorVersion());
    }

    //This is neither pretty nor efficient, but it will only happen once, at start up, so as long as it's
    //correct it should be fine.
    //configString is parsed as a | separated string, where \ functions to quote the following character.
    //A trailing | does not contribute anything - a leading | contributes an empty value.
    protected ArrayList<String> parseConfigHeaders(String configString) {
        ArrayList<String> values = new ArrayList<String>();
        boolean onBackslash = false;
        String accumulator = new String();
        String rest = new String(configString);
        while (rest.length() > 0) {
            Character c = rest.charAt(0);
            rest = rest.substring(1);
            if (onBackslash) {
                accumulator = accumulator + c;
                onBackslash = false;
            } else {
                if (c == '\\') {
                    onBackslash = true;
                } else if (c == '|') {
                    values.add(accumulator);
                    accumulator = new String();
                } else {
                    accumulator = accumulator + c;
                }
            }
        }
        if (accumulator.length() > 0)
            values.add(accumulator);
        return values;
    }
}

