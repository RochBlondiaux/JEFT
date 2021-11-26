package me.rochblondiaux.client;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ClientLauncher {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        new Client();
    }
}
