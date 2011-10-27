package edu.illinois.medusa;

import com.caringo.client.ScspHeaders;

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

    protected CaringoHints() {
        super();
    }

    protected CaringoHints(Map<String, String> hints) {
        super(hints);
    }

    //Merge some supplied hints. Note that supplied hints take precedence over
    //hints already in the hash. Note also that we provide different methods here
    //depending on whether hints is already a CaringoHints or not. If not, we assume
    //that the values are single and have no URL encoding.
    public CaringoHints merge_hints(Map<String, String> hints) {
        if (hints != null) {
            for (Map.Entry<String, String> entry : hints.entrySet()) {
                this.addHint(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    //If we are trying to merge other CaringoHints in then they are already in the quoted form, so
    //we want to take that into account.
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

    public CaringoHints copy() {
        return new CaringoHints(this);
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
        try {
            String quotedValue = URLEncoder.encode(value, "UTF-8");
            if (this.containsKey(key)) {
                this.put(key, this.get(key) + ":" + quotedValue);
            } else {
                this.put(key, quotedValue);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

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
