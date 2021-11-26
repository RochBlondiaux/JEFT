package me.rochblondiaux.server.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class NetworkUtils {

    public static boolean isPortAvailable(InetSocketAddress address) {
        try (Socket ignored = new Socket(address.getHostName(), address.getPort())) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

}
