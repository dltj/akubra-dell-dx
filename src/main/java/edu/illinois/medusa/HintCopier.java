package edu.illinois.medusa;

import com.caringo.client.ScspHeaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * Class for copying hints from one HintedBlob to another. The idea is that the copier may have a lot of rules.
 * For each hint/header we try to find a rule that tells us to copy or not to copy that hint. For now the focus
 * is on something that is simple but still fits our use case. Obviously this could be a lot more complex if needed.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class HintCopier {

    /**
     * List to hold the rules. We provide ways to add rules to the front and back.
     */
    protected List<HintCopyRule> copyRules;

    /**
     * Construct a new HintCopier with no rules
     */
    protected HintCopier() {
        this.copyRules = new ArrayList<HintCopyRule>();
    }

    /**
     * Add a rule to the end of the rule list.
     *
     * @param rule Rule to be added
     */
    protected void addRule(HintCopyRule rule) {
        this.copyRules.add(rule);
    }

    /**
     * Add a rule to the beginning of the rule list
     *
     * @param rule Rule to be added
     */
    protected void addRuleFront(HintCopyRule rule) {
        this.copyRules.add(0, rule);
    }

    /**
     * Remove any rules with the provided name from the rule list
     *
     * @param name Name of rules to remove
     */
    protected void removeRule(String name) {
        ListIterator<HintCopyRule> i = this.copyRules.listIterator();
        while (i.hasNext()) {
            HintCopyRule rule = i.next();
            if (rule.name.equals(name))
                i.remove();
        }
    }

    /**
     * Copy headers from a blob (that has already interacted with Caringo) to another blob (by adding values as hints)
     * using the rules defined by this HintCopier.
     *
     * @param source Blob from which to copy headers. Must have a current response, i.e. must have interacted with Caringo storage
     * @param target Blob to which to copy headers
     */
    protected void copyHeaders(HintedBlob source, HintedBlob target) {
        if (source.response != null) {
            ScspHeaders headers = source.response().scspResponse().getResponseHeaders();
            HashMap<String, ArrayList<String>> header_map = headers.getHeaderMap();
            for (String key : header_map.keySet()) {
                if (doCopy(key)) {
                    for (String value : header_map.get(key)) {
                        target.addHint(":" + key, value);
                    }
                }
            }
        }
    }

    /**
     * Return whether or not to copy a header with the given name.
     * If a matching rule is found then returns true or false as depending on whether the rule is for rejection or
     * acceptance of the header. If no match is found then returns false.
     * @param header_name
     * @return
     */
    protected boolean doCopy(String header_name) {
        for (HintCopyRule rule : this.copyRules) {
            HintCopyAction copy = rule.copyHeader(header_name);
            if (copy == HintCopyAction.ACCEPT)
                return true;
            if (copy == HintCopyAction.REJECT)
                return false;
        }
        return false;
    }
}
