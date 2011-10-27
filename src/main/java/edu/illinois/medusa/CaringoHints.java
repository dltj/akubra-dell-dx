package edu.illinois.medusa;

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
}
