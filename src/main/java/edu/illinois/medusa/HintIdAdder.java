package edu.illinois.medusa;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/30/11
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class HintIdAdder extends HintAdder {

    public void addHints(HintedBlob blob) {
        blob.addHint("fedora:stream-id", blob.getId().toString());
    }
}
