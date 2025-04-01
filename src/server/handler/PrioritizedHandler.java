package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String[] path) throws IOException {
        if (path[path.length - 1].equals("prioritized") && path.length == 2) {
            String response = gson.toJson(taskManager.getPrioritizedTasks());
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            sendText(exchange, response, 200);
        } else {
            sendText(exchange, "Неизвестный запрос.", 400);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String[] path) throws IOException {
        sendText(exchange, "Неверный метод.", 405);
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String[] path) throws IOException {
        sendText(exchange, "Неверный метод.", 405);
    }
}