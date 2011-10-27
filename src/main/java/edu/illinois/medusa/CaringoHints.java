package edu.illinois.medusa;

import com.caringo.client.ScspHeaders;

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
        if(hints != null)
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
        for(Map.Entry<String, String> entry : this.entrySet()) {
            headers.addValue(metadataHeaderName(entry.getKey()), entry.getValue());
        }
    }

    //The name value should be of the form "namespace:key"
    //This gets mapped to x-namespace-meta-key
    protected String metadataHeaderName(String name) {
        int splitPosition = name.indexOf(':');
        String namespace = name.substring(0, splitPosition);
        String key = name.substring(splitPosition + 1);
        return "x-" + namespace + "-meta-" + key;
    }
}
