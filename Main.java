import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

    HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

    // /health
    server.createContext("/health", exchange -> {
      try {
        respondJson(exchange, 200, "{\"status\":\"UP\"}");
      } catch (IOException e) {
        safeClose(exchange);
      }
    });

    // /api/hello
    server.createContext("/api/hello", exchange -> {
      try {
        respondJson(exchange, 200, "{\"message\":\"Hola desde Java microservicio\"}");
      } catch (IOException e) {
        safeClose(exchange);
      }
    });

    // /
    server.createContext("/", exchange -> {
      try {
        respondJson(exchange, 200, "{\"service\":\"java-ms\",\"endpoints\":[\"/health\",\"/api/hello\"]}");
      } catch (IOException e) {
        safeClose(exchange);
      }
    });

    server.setExecutor(null);
    server.start();

    System.out.println("Java microservice running on port " + port);
  }

  // ✅ Ahora SOLO lanza IOException (lo que el handler sí permite manejar)
  private static void respondJson(HttpExchange exchange, int status, String body) throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
    exchange.sendResponseHeaders(status, bytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(bytes);
    }
  }

  // Cierra seguro si algo falla antes de responder bien
  private static void safeClose(HttpExchange exchange) {
    try {
      exchange.sendResponseHeaders(500, -1);
    } catch (Exception ignored) {}
    try {
      exchange.close();
    } catch (Exception ignored) {}
  }
}
