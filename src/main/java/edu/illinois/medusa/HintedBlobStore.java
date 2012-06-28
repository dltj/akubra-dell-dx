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
    /**
     * HintCopier that will control copying of blobs from this store
     */
    protected HintCopier hintCopier;

    /**
     * Construct a new HintedBlobStore
     *
     * @param storeId        Arbitrary name for the blob store
     * @param configFilePath Path to properties file with configuration information for the blob store
     */
    protected HintedBlobStore(URI storeId, String configFilePath) {
        super(storeId, configFilePath);
    }

    protected void initializeHintCopier() {
        this.hintCopier = new HintCopier();
        this.hintCopier.addRule(new HintCopyRegexpRule("caringo-meta", true, "^x-.+-meta-.+$"));
        this.hintCopier.addRule(new HintCopyRegexpRule("caringo-lifepoint", true, "^Lifepoint$"));
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

    /**
     * Configuration from configuration file.
     *
     * @param config Properties used to configure the BlobStore
     */
    protected void configFromProperties(Properties config) {
        this.hints = new CaringoHints();
        super.configFromProperties(config);
        this.initializeHintCopier();
        this.addCreatorHeaders();
        this.addConfigHeaders(config);
    }

    /**
     * Add headers/hints based on configuration file.
     * Each property of the form header.x = y (where "header" is literal) gives rise to a header with
     * name x and value y. Multiple values can be specified as y|z|w, and the \ character serves to quote the
     * following character. Note that each \ may need another \ to quote itself to satisfy the properties file
     * syntax. E.g. abc\\\\|de\\|fg in the properties file represents the string abc\\|de\|fg passed to this plugin,
     * which parses left to right and sees the two values abc\ and de|fg.
     *
     * These headers/hints will automatically be ignored when copying hints unless something else modifies the HintCopier
     * to change that.
     *
     * @param config Properties used to configure BlobStore
     */
    protected void addConfigHeaders(Properties config) {
        Set<String> keys = config.stringPropertyNames();
        for (String key : keys) {
            if (key.startsWith("header.")) {
                String headerName = key.substring("header.".length());
                List<String> values = parseConfigHeaders(config.getProperty(key));
                for (String value : values) {
                    this.hints.addHint(":" + headerName, value);
                }
                this.hintCopier.addRule(new HintCopyRegexpRule(key, false, "^" + java.util.regex.Pattern.quote(headerName) + "$"));
            }
        }
    }

    /**
     * Add some headers identifying this akubra plugin as the creator of objects in storage. Requested by Dell.
     */
    protected void addCreatorHeaders() {
        this.hints.addHint(":x-Dell-creator-meta", AkubraPlugin.dellCreator);
        this.hints.addHint(":x-Dell-creator-version-meta", AkubraPlugin.dellCreatorVersion());
    }

    //This is neither pretty nor efficient, but it will only happen once, at start up, so as long as it's
    //correct it should be fine.
    //configString is parsed as a | separated string, where \ functions to quote the following character.
    //A trailing | does not contribute anything - a leading | contributes an empty value.

    /**
     * Parse a value passed through the properties file into a set of values.
     *
     * @param configString Raw value to be parsed
     * @return Array of parsed values
     */
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

    protected HintCopier getHintCopier() {
        return this.hintCopier;
    }

}

