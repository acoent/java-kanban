package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import server.adapter.DurationAdapter;
import server.adapter.LocalDateTimeAdapter;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).create();

    protected BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public final void handle(HttpExchange exchange) throws IOException {
        try {
            String[] path = exchange.getRequestURI().getPath().split("/");
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange, path);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendError(exchange, 405);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, e.getMessage(), 500);
        }
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        final String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    protected abstract void handleGet(HttpExchange exchange, String[] path) throws IOException;

    protected abstract void handlePost(HttpExchange exchange, String[] path) throws IOException;

    protected abstract void handleDelete(HttpExchange exchange, String[] path) throws IOException;

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendError(HttpExchange exchange, int statusCode) throws IOException {
        String errorMessage = "{\"error\": \"Error: " + statusCode + "\"}";
        sendText(exchange, errorMessage, statusCode);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendError(exchange, 404); // Not Found
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendError(exchange, 406); // Not Acceptable
    }
}
