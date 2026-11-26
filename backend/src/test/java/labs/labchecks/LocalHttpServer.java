package labs.labchecks;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import com.sun.net.httpserver.HttpServer;

/** Tiny HTTP server on 127.0.0.1 used by the lab checks (an "internal" service or a partner API). */
final class LocalHttpServer implements AutoCloseable {
    record Reply(int status, String body, long delayMillis) {}

    private final HttpServer server;
    private final AtomicInteger hits = new AtomicInteger();

    LocalHttpServer(Supplier<Reply> replies) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/", exchange -> {
            hits.incrementAndGet();
            Reply r = replies.get();
            try {
                if (r.delayMillis() > 0) Thread.sleep(r.delayMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            byte[] bytes = r.body().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(r.status(), bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            } catch (IOException ignored) {
                // client gave up (timeout); that is the point of some checks
            }
        });
        server.start();
    }

    String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    int hits() {
        return hits.get();
    }

    void resetHits() {
        hits.set(0);
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
