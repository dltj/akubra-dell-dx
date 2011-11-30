package edu.illinois.medusa;

import com.caringo.client.ScspHeader;
import com.caringo.client.ScspHeaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 11/30/11
 * Time: 10:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class HintCopier {

    protected List<HintCopyRule> copyRules;

    protected HintCopier() {
        this.copyRules = new ArrayList<HintCopyRule>();
    }

    protected void addRule(HintCopyRule rule) {
        this.copyRules.add(rule);
    }

    protected void addRuleFront(HintCopyRule rule) {
        this.copyRules.add(0, rule);
    }

    protected void removeRule(String name) {
        ListIterator<HintCopyRule> i = this.copyRules.listIterator();
        while(i.hasNext()) {
            HintCopyRule rule = i.next();
            if (rule.name.equals(name))
                i.remove();
        }
    }

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

    protected boolean doCopy(String header_name) {
        for(HintCopyRule rule : this.copyRules) {
            HintCopyAction copy = rule.copyHeader(header_name);
            if (copy == HintCopyAction.ACCEPT)
                return true;
            if (copy == HintCopyAction.REJECT)
                return false;
        }
        return false;
    }
}
