package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.adapter.DurationAdapter;
import server.adapter.LocalDateTimeAdapter;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).create();

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String historyJson = gson.toJson(taskManager.getHistory());
            exchange.sendResponseHeaders(200, historyJson.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(historyJson.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
