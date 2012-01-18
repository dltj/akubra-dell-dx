package edu.illinois.medusa;

/**
 * Base class for rules to determine whether to copy a header.
 *
 * Rules have a name (arbitrary) so that a HintCopier can manipulate them by name if needed
 * The also have a flag that determines whether to accept or reject if the rule applies
 *
 * This base rule by default passes on every header, i.e. this rule never matches
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class HintCopyRule {

    /**
     * Name for the rule - usable by HintCopier to manipulate its list of rules
     */
    protected String name;
    /**
     * If true then if this rule applies the hint should be copied. If false then if this rule applies the hint
     * should not be copied.
     */
    protected boolean accept_header;

    /**
     * Construct a new rule
     *
     * @param name Name for this rule
     * @param accept_header If true copy the header if the rule applies; if false do not copy the header if the rule applies
     */
    protected HintCopyRule(String name, boolean accept_header) {
        this.name = name;
        this.accept_header = accept_header;
    }

    /**
     * Return whether this rule wants to take action on this header
     *
     * @param header_name Name of the header to check
     * @return Whether to accept or reject the header or to move on the next rule
     */
    public HintCopyAction copyHeader(String header_name) {
        return HintCopyAction.PASS;
    }

}
