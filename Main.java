import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

    HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

    // Endpoint de salud típico para microservicios
    server.createContext("/health", exchange ->
        respondJson(exchange, 200, "{\"status\":\"UP\"}")
    );

    // Endpoint ejemplo
    server.createContext("/api/hello", exchange ->
        respondJson(exchange, 200, "{\"message\":\"Hola desde Java microservicio\"}")
    );

    // Default
    server.createContext("/", exchange ->
        respondJson(exchange, 200, "{\"service\":\"java-ms\",\"endpoints\":[\"/health\",\"/api/hello\"]}")
    );

    server.setExecutor(null); // usa executor por defecto
    server.start();

    System.out.println("Java microservice running on port " + port);
  }

  private static void respondJson(HttpExchange exchange, int status, String body) throws Exception {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
    exchange.sendResponseHeaders(status, bytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(bytes);
    }
  }
}
