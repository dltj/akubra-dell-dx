package edu.illinois.medusa;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/30/11
 * Time: 10:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class HintCopyRule {

    protected String name;
    //if this is true when the rule matches the header is accepted for copying - if false it is rejected for copying
    protected boolean accept_header;

    protected HintCopyRule(String name, boolean accept_header) {
        this.name = name;
        this.accept_header = accept_header;
    }

    //whether or not to copy header, based on whether the rule matches and whether accept_header is true or false.
    //True means accept the header
    //False means reject the header
    //null means the rule does not have an opinion on whether to copy
    public HintCopyAction copyHeader(String header_name) {
        return HintCopyAction.PASS;
    }

}
