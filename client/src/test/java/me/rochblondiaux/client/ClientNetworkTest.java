package me.rochblondiaux.client;

import me.rochblondiaux.client.network.netty.NettyClient;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ClientNetworkTest {

    static NettyClient nettyClient;

    /*
    @BeforeAll
    static void beforeAll() {
        nettyClient = new NettyClient();
        nettyClient.init();
        nettyClient.connect("localhost", 9999);
    }

    @Test
    public void testLoginRequestPacket() {
        NettyServer server = nettyClient.getConnectionManager().getServer();
        while (server == null) {
            server = nettyClient.getConnectionManager().getServer();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        server.addPacketToQueue(new LoginRequestPacket());
    }

    @AfterAll
    static void afterAll() {
        nettyClient.stop();
    }

     */
}
