package client;

import org.junit.Test;
import server.MessageEgressVerticle;

public class MessageConsumerClientTest {

    @Test
    public void testMessageEgress() throws Exception {
        // Deploy the server
        MessageEgressVerticle.deply();

        // Run the client
        MessageConsumerClient client = new MessageConsumerClient();
        client.startSessionHttpClient();

        // Thread sleep to observe SSEs on client.
        Thread.sleep(20 * 1000);
    }
}
