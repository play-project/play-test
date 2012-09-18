/**
 * 
 */
package org.ow2.play.test.pubsub.subscriber;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;

/**
 * @author chamerling
 *
 */
public class Stats {
    
    private static Stats INSTANCE;
    
    public long nb = 0;
    
    public long startTime;
    
    public static Stats get() {
        if (INSTANCE == null) {
            INSTANCE = new Stats();
        }
        return INSTANCE;
    }
    
    public synchronized void request(Notify notify) {
        nb++;
    }

}
