package edu.illinois.medusa;

import com.caringo.enumerator.EnumeratorEntry;
import com.caringo.enumerator.EnumeratorResponse;
import com.caringo.enumerator.EnumeratorType;
import com.caringo.enumerator.ObjectEnumerator;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: hading
 * Date: 3/30/12
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnumTester {

    public static void main(String[] argv) throws Exception {
        final String host = "172.22.70.6";
        final int port = 8080;
        final String channel = "development-test-repo";
        final Long maxItems = 1L;
        Date startDate = new Date(2000, 1, 1);
        Date endDate = new Date(2099, 1, 1);
        HashMap<String, String> queryArgs = new HashMap<String, String>();
        Long timeout = 3600L;

        ObjectEnumerator enumerator = null;
        EnumeratorResponse startResponse;
        EnumeratorResponse itemResponse;

        try {
            enumerator = new ObjectEnumerator(host, port, EnumeratorType.ENUM_TYPE_METADATA);
            //startResponse = enumerator.start(channel);
            startResponse = enumerator.start(channel, EnumeratorType.ENUM_TYPE_METADATA, null, null, timeout, "Java", queryArgs);
            System.out.println("Enumerator UUID: " + enumerator.getUUID());
            itemResponse = enumerator.next(maxItems, queryArgs);
            int count = 0;
            while (itemResponse != null && count < 1000) {
                count++;
                itemResponse = enumerator.next(maxItems, queryArgs);
                System.out.println("Try: " + count);
                if (itemResponse.getEntries().size() > 0) {
                    EnumeratorEntry entry = itemResponse.getEntries().get(0);
                    System.out.println("Object UUID: " + entry.getUuid());
                    System.out.println(itemResponse.getResponseBody());
                } else {
                    System.out.println("Iteration done: " + count);
                    break;
                }
            }
            //BREAKPOINT NEXT LINE
            System.out.println("Made call on enumerator.");
        } finally {
            enumerator.end();
        }
    }

}
