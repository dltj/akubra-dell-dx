package edu.illinois.medusa;

/**
 * This type of rule tries to match a header against a regexp. If it matches then it (depending on how it is configured)
 * asks for a acceptance or rejection. If it doesn't match then it passes.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class HintCopyRegexpRule extends HintCopyRule {
    protected String regexp;

    /**
     * Construct a new rule
     *
     * @param name A name for this rule
     * @param accept_header If true accept the header if matched; if false reject the header if matched
     * @param regexp The regexp to match against the header
     */
    protected HintCopyRegexpRule(String name, boolean accept_header, String regexp) {
        super(name, accept_header);
        this.regexp = regexp;
    }

    /**
     * Return what to do for a given header
     *
     * @param header_name Name of header to check
     * @return Whether to accept or reject the header or to move on to the next rule
     */
    public HintCopyAction copyHeader(String header_name) {
        if (header_name.matches(regexp))
            return (this.accept_header ? HintCopyAction.ACCEPT : HintCopyAction.REJECT);
        return HintCopyAction.PASS;
    }
}
