package server;

import java.net.HttpURLConnection;
import java.util.Arrays;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * This class contains the server side implementation of SSE(Server Sent Event).
 * When server receives a post request from the client, it sends 10 messages each of size
 * {@link MessageEgressVerticle#MESSAGE_SIZE}. Each Message is written in a {@link HttpServerResponse#write(String)}
 * Each write call is one record for us and needs to identify its boundaries.
 */

public class MessageEgressVerticle extends AbstractVerticle {

    public static final String SELF_LINK = "/vertx/message-egress";
    private static final Logger logger = LoggerFactory.getLogger(SELF_LINK);
    private static final int MESSAGE_SIZE = 10 * 1024;

    private Router router;
    private int port;

    public MessageEgressVerticle(int port) {
        this.router = Router.router(vertx);
        this.port = port;
    }

    @Override
    public void start(Promise<Void> promise) {
        this.router.route(HttpMethod.POST, SELF_LINK)
                .handler(BodyHandler.create(false))
                .handler(this::handlePost);
        this.router.route(SELF_LINK)
                .handler(this::handleRequest);

        HttpServer server = vertx.createHttpServer();

        server.requestHandler(this.router).listen(port, ar -> {
            if (ar.succeeded()) {
                logger.info("HTTP server running on port " + port);
                promise.complete();
            } else {
                logger.error("Could not start a HTTP server", ar.cause());
                promise.fail(ar.cause());
            }
        });
    }

    public void handlePost(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.setChunked(true);
        response.headers().add("Transfer-Encoding", "chunked");
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
        response.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        response.headers().add(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
        response.setStatusCode(HttpURLConnection.HTTP_OK);

        response.closeHandler(aVoid -> stopSession(response));

        response.write(Buffer.buffer());

        String event = generateEvent(MESSAGE_SIZE);
        for (int i = 1; i <= 10; i++) {
            context.response().write(Json.encodeToBuffer(event), ar -> {
                if (ar.failed()) {
                    String errorMsg = String.format("Message push failed for error %s", ar.cause().toString());
                    logger.error(errorMsg);
                } else {
                    logger.debug("Message sent successfully...");
                }
            });
        }

    }

    private String generateEvent(int len) {
        char[] charArray = new char[len];
        Arrays.fill(charArray, 'a');
        return new String(charArray);
    }

    private void handleRequest(RoutingContext context) {
        context.response().setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
        context.response().end("Action not supported");
    }

    private void stopSession(HttpServerResponse sessionResponse) {
        logger.info("Stopping session");
        sessionResponse.end();
    }

    public static void main(String[] args) {
        deply();
    }

    public static void deply() {
        Vertx vertx = Vertx.vertx(new VertxOptions());
        vertx.deployVerticle(() -> new MessageEgressVerticle(8000), new DeploymentOptions().setInstances(1));
    }

}
