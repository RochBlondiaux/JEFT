package me.rochblondiaux.server;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ServerLauncher {

    public static void main(String[] args) throws Exception {
        String hostname = "localhost";
        if (args.length >= 1) hostname = args[0];
        int port = 9999;
        if (args.length == 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
            }
        }
        new Server(hostname, port);
    }
}
