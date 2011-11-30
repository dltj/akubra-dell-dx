package edu.illinois.medusa;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/30/11
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class HintCopyRegexpRule extends HintCopyRule {
    protected String regexp;

    protected HintCopyRegexpRule(String name, boolean accept_header, String regexp) {
        super(name, accept_header);
        this.regexp = regexp;
    }

    public HintCopyAction copyHeader(String header_name) {
        if (header_name.matches(regexp))
            return (this.accept_header ? HintCopyAction.ACCEPT : HintCopyAction.REJECT);
        return HintCopyAction.PASS;
    }
}
