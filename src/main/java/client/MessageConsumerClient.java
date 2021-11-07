package client;

import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import server.MessageEgressVerticle;

/**
 * This class contains the client which receives SSE(Server Sent Events.)
 */
public class MessageConsumerClient {

    private static final Logger logger = Logger.getLogger(MessageConsumerClient.class.getName());

    private HttpClientRequest request;
    private final HttpClient client;
    private final String serverUri = "http://localhost:8000" + MessageEgressVerticle.SELF_LINK;

    public MessageConsumerClient() {
        this.client = createHttpClient();
    }

    public void startSessionHttpClient() {
        String reconnectionMsg = "Connection with gateway lost, reconnecting";
        this.request = client.postAbs(this.serverUri,
                httpClientResponse -> {
                    if (httpClientResponse.statusCode() == HttpURLConnection.HTTP_OK) {
                        logger.log(Level.INFO, String.format("Successfully connected to %s", this.serverUri));
                    }
                    httpClientResponse.handler(this::handleServerSentEvent);
                    httpClientResponse.endHandler(aVoid -> {
                        logger.info(reconnectionMsg);
                        // Reconnect.
                    });
                });
        this.request.exceptionHandler(throwable -> {
            logger.info(reconnectionMsg);
            // Reconnect.
        });
        this.request.connectionHandler(httpConnection -> {
            httpConnection.closeHandler(aVoid -> {
                logger.info(reconnectionMsg);
                // Reconnect.
            });
            httpConnection.exceptionHandler(throwable -> {
                logger.info(reconnectionMsg);
                // Reconnect.
            });
        });
        this.request.end();
    }

    private void handleServerSentEvent(Buffer chunk) {
        logger.info("Chunk size " + chunk.length());
    }

    private HttpClient createHttpClient() {
        Vertx vertx = Vertx.vertx();
        HttpClientOptions httpClientOptions = new HttpClientOptions();
        httpClientOptions.setMaxPoolSize(5);
        // Max chunk size of 64kb.
        httpClientOptions.setMaxChunkSize(64 * 1024);
        httpClientOptions.setSsl(true);

        return vertx.createHttpClient(httpClientOptions);
    }

}
