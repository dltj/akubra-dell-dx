package edu.illinois.medusa;

import com.caringo.client.ScspHeaders;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulate and work with hints. The idea is to use the Akubra concept of hints to help manage
 * Caringo headers. We account for the possibility of multiple values for the same header.
 * <p/>
 * Basically this extends HashMap<String, String> (in order to be compatible with Akubra hints).
 * The value string is a ':' separated string containing all values for the key. Each individual value is
 * URL encoded (so that it may itself contain ':' if needed). The methods automatically keep this up to date
 * in the right way.
 * <p/>
 * The keys should take one of two forms. A key of the form ':key-name' will get mapped directly to headers 'key-name'.
 * A key of the form 'namespace:key' will get mapped to a header of the form 'x-namespace-meta-key'. For each key
 * we generate a header for each value.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class CaringoHints extends HashMap<String, String> {

    /**
     * Construct an empty object
     */
    protected CaringoHints() {
        super();
    }

    /**
     * Construct an object with some starting hints.
     *
     * @param hints Initial hints
     */
    protected CaringoHints(Map<String, String> hints) {
        super(hints);
    }

    /**
     * Merge some supplied hints. Supplied hints take precedence over
     * hints already in the hash.
     *
     * @param hints These are assumed to be key -> single value
     * @return this with hints added
     */
    public CaringoHints merge_hints(Map<String, String> hints) {
        if (hints != null) {
            for (Map.Entry<String, String> entry : hints.entrySet()) {
                this.addHint(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     * Merge some supplied hints. Supplied hints take precedence over
     * hints already in the hash.
     *
     * @param hints These are already in proper CaringoHints form, so may be multivalued.
     * @return this with hints added
     */
    public CaringoHints merge_hints(CaringoHints hints) {
        if (hints != null) {
            for (String key : hints.keySet()) {
                String[] values = hints.getValues(key);
                for (String value : values) {
                    this.addHint(key, value);
                }
            }
        }
        return this;
    }

    /**
     * Copy this object
     *
     * @return A copy of this object
     */
    public CaringoHints copy() {
        return new CaringoHints(this);
    }

    /**
     * Copy this object and merge supplied hints into copy
     *
     * @param hints Hints to be merged into copy
     * @return Copy of this object with merged hints
     */
    public CaringoHints copy_and_merge_hints(Map<String, String> hints) {
        return this.copy().merge_hints(hints);
    }


    /**
     * Add a hint
     *
     * @param key   Key for hint to be added
     * @param value Value for hint to be added
     */
    public void addHint(String key, String value) {
        String quotedValue = urlEncode(value);
        if (this.containsKey(key)) {
            this.put(key, this.get(key) + ":" + quotedValue);
        } else {
            this.put(key, quotedValue);
        }
    }

    /**
     * Get all hint values for this key
     *
     * @param key Key for hint
     * @return Array of string values of the key.
     */
    public String[] getValues(String key) {
        try {
            if (this.containsKey(key)) {
                String[] values = this.get(key).split(":");
                for (int i = 0; i < values.length; i++) {
                    values[i] = URLDecoder.decode(values[i], "UTF-8");
                }
                return values;
            } else {
                return (new String[0]);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * Convert a key to a header name.
     * The key value should be either of the form:
     * "namespace:name" - This gets mapped to x-namespace-meta-name
     * ":name" - This gets mapped directly to name
     *
     * @param key Hint key
     * @return Header name suitable to pass to Caringo storage
     */
    protected String metadataHeaderName(String key) {
        int splitPosition = key.indexOf(':');
        if (splitPosition > 0) {
            String namespace = key.substring(0, splitPosition);
            String name = key.substring(splitPosition + 1);
            return "x-" + namespace + "-meta-" + name;
        } else {
            return key.substring(1);
        }
    }

    /**
     * Add header/value pairs to headers for each value contained in this object.
     *
     * @param headers Headers object to augment
     */
    public void augmentScspHeaders(ScspHeaders headers) {
        for (Map.Entry<String, String> entry : this.entrySet()) {
            String headerName = metadataHeaderName(entry.getKey());
            for (String value : this.getValues(entry.getKey())) {
                headers.addValue(headerName, value);
            }
        }
    }

    /**
     * URL encode a String
     *
     * @param value String to be encoded
     * @return Encoded string
     */
    protected String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}
