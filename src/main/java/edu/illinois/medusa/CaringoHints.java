package edu.illinois.medusa;

import com.caringo.client.ScspHeaders;

import javax.print.DocFlavor;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 10/27/11
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoHints extends HashMap<String, String> {


    //Merge some supplied hints. Note that supplied hints take precedence over
    //hints already in the hash
    public CaringoHints merge_hints(Map<String, String> hints) {
        if (hints != null)
            this.putAll(hints);
        return this;
    }

    public CaringoHints copy() {
        CaringoHints copy = new CaringoHints();
        copy.putAll(this);
        return copy;
    }

    public CaringoHints copy_and_merge_hints(Map<String, String> hints) {
        return this.copy().merge_hints(hints);
    }

    public void augmentScspHeaders(ScspHeaders headers) {
        for (Map.Entry<String, String> entry : this.entrySet()) {
            String headerName = metadataHeaderName(entry.getKey());
            for (String value : this.getValues(entry.getKey())) {
                headers.addValue(headerName, value);
            }
        }
    }

    public void addHint(String key, String value) {
        String quotedValue = URLEncoder.encode(value);
        if (this.containsKey(key)) {
            this.put(key, this.get(key) + ":" + quotedValue);
        } else {
            this.put(key, quotedValue);
        }
    }

    public String[] getValues(String key) {
        if (this.containsKey(key)) {
            String[] values = this.get(key).split(":");
            for (int i = 0; i < values.length; i++) {
                values[i] = URLDecoder.decode(values[i]);
            }
            return values;
        } else {
            return (new String[0]);
        }
    }

    //The name value should be either of the form:
    // "namespace:key" - This gets mapped to x-namespace-meta-key
    // ":key" - This gets mapped directly to key
    protected String metadataHeaderName(String name) {
        int splitPosition = name.indexOf(':');
        if (splitPosition > 0) {
            String namespace = name.substring(0, splitPosition);
            String key = name.substring(splitPosition + 1);
            return "x-" + namespace + "-meta-" + key;
        } else {
            return name.substring(1);
        }
    }
}
