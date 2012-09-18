/**
 * 
 * Copyright (c) 2012, PetalsLink
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 */
package org.ow2.play.test.pubsub.notify;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.client.http.simple.HTTPConsumerClient;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class Main {

    static {
        // WTF?
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }

    public static void main(String[] args) {

        if (args == null || args.length < 6) {
            System.err.println("!!! Bad number of arguments");
            usage();
            System.exit(-1);
        }

        String url = args[0];

        try {
            new URL(url);
        } catch (MalformedURLException e2) {
            System.err.println("Bad URL");
            System.exit(-1);
        }

        String name = args[1];
        String ns = args[2];
        String prefix = args[3];

        int nb = 10;
        if (args.length >= 5) {
            nb = Integer.parseInt(args[4]) > 0 ? Integer.parseInt(args[4]) : 10;
        }

        int delay = 100;
        if (args.length >= 6) {
            delay = Integer.parseInt(args[5]) > 0 ? Integer.parseInt(args[5]) : 100;
            if (delay < 100) {
                delay = 100;
                System.err.println("Set the delay at 100ms, less is not possible...");
            }
        }

        QName topic = new QName(ns, name, prefix);
        System.out.printf("Trying to send WSN Notify messages to %s...", url);
        System.out.println("");
        System.out.printf("Using topic %s", topic.toString());
        System.out.println("");

        HTTPConsumerClient client = new HTTPConsumerClient(url);
        for (int i = 0; i < nb; i++) {

            String message = String.format("Message %d out of %d sent at %s", i + 1, nb,
                    System.currentTimeMillis());
            
            System.out.println("Sending message : " + message);
            try {
                client.notify(XMLHelper.createDocumentFromString("<cli>" + message + "</cli>"),
                        topic);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e1) {
            }
        }

        System.exit(0);
    }

    public static final void usage() {
        System.out.println("notifycli <provider url> <topic name> <topic ns> <topic prefix> "
                + "[nb] [delay]");
    }

}
