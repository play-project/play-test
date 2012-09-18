/**
 * 
 */
package org.ow2.play.test.pubsub.subscriber;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.xml.namespace.QName;

import jline.console.ConsoleReader;

/**
 * @author chamerling
 * 
 */
public class Main {

    static PubSubClientServer pubSubClientServer;

    static boolean started = false;

    /**
     * args[0] = local IP args[1] = local (free) port args[2] = provider URL
     * args[3] = Topic name args[4] = Topic URL args[5] = Topic Prefix
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException {

        if (args == null || args.length < 6) {
            System.err.println("!!! Bad number of arguments");
            usage();
            System.exit(-1);
        }

        String host = args[0];
        String port = args[1];

        String me = "http://" + host + ":" + port + "/pubsubcli/Service";

        String provider = args[2];
        QName topic = new QName(args[4], args[3], args[5]);

        ConsoleReader reader = new ConsoleReader();
        reader.setPrompt("> ");

        String line;
        PrintWriter out = new PrintWriter(reader.getOutput());

        while ((line = reader.readLine()) != null) {
            if (line.equals("start")) {
                if (started) {
                    out.println("Already started!");
                } else {
                    start(out, me, provider, topic);
                    started = true;
                }
            } else if (line.equals("stop")) {
                if (!started) {
                    out.println("Start it before stopping!");
                } else {
                    stop();
                    started = false;
                }
            } else if (line.equals("i") || line.equals("info")) {
                out.println("Configuration:");
                out.println(" - Local : " + me);
                out.println(" - Remote : " + provider);
                out.println(" - Topic : " + topic);
                out.println("Statistics:");
                out.println(" - Nb requests : " + Stats.get().nb);
                out.println(" - Start time : " + new Date(Stats.get().startTime));
            } else if (line.equals("quit")) {
                out.println("Exiting...");
                if (started) stop();
                break;
            }
        }
    }

    public static final void usage() {
        System.out
                .println("pubsubcli <local ip> <local port> <provider url> <topic name> <topic ns> <topic prefix>");
    }

    private static void start(PrintWriter pw, String me, String provider, QName topic) {
        if (me != null && provider != null && topic != null) {
            pubSubClientServer = new PubSubClientServer(pw, topic, provider, me);
            pubSubClientServer.start();
        }
    }

    private static void stop() {
        if (pubSubClientServer != null)
            pubSubClientServer.stop();
    }

}
