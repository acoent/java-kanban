package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import exception.TimeIntersectionException;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String[] path) throws IOException {
        if (path[path.length - 1].equals("subtasks") && path.length == 2) {
            handleGetSubtasks(exchange);
            System.out.println("Запрос GET /subtasks обработан.");
        } else if (path.length == 3) {
            handleGetSubtaskById(exchange);
            System.out.println("Запрос GET /subtasks/{id} обработан.");
        } else {
            sendText(exchange, "Неверный запрос.", 400);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        final String response = gson.toJson(taskManager.getSubtasks());
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        sendText(exchange, response, 200);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        final Optional<Integer> subtaskIdOpt = getId(exchange);
        if (subtaskIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный id подзадачи", 400);
            return;
        }
        try {
            final Subtask subtask = taskManager.getSubtaskById(subtaskIdOpt.get());
            final String response = gson.toJson(subtask);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            sendText(exchange, response, 200);
        } catch (IllegalArgumentException e) {
            sendText(exchange, e.getMessage(), 404);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String[] path) throws IOException {
        if (path[path.length - 1].equals("subtasks") && path.length == 2) {
            handlePostSubtask(exchange);
            System.out.println("Запрос POST /subtasks обработан.");
        } else {
            sendText(exchange, "Неизвестный запрос.", 400);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        final Subtask newSubtask = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Subtask.class);
        if (newSubtask == null) {
            sendText(exchange, "Отправлен пустой запрос.", 400);
            return;
        }
        if (newSubtask.getId() == 0) {
            try {
                taskManager.addSubtask(newSubtask);
                sendText(exchange, "Новая подзадача сохранена.", 201);
            } catch (ManagerSaveException e) {
                sendText(exchange, e.getMessage(), 500);
            } catch (TimeIntersectionException e) {
                sendText(exchange, e.getMessage(), 406);
            }
        } else {
            try {
                taskManager.subtaskUpdate(newSubtask.getId(), newSubtask);
                sendText(exchange, "Подзадача с id " + newSubtask.getId() + " обновлена.", 201);
            } catch (ManagerSaveException e) {
                sendText(exchange, e.getMessage(), 500);
            } catch (IllegalArgumentException e) {
                sendText(exchange, e.getMessage(), 404);
            } catch (TimeIntersectionException e) {
                sendText(exchange, e.getMessage(), 406);
            }
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String[] path) throws IOException {
        if (path.length == 3) {
            handleDeleteSubtask(exchange);
            System.out.println("Запрос DELETE /subtasks/{id} обработан.");
        } else {
            sendText(exchange, "Неизвестный запрос.", 400);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        final Optional<Integer> subtaskId = getId(exchange);
        if (subtaskId.isEmpty()) {
            sendText(exchange, "Некорректный id подзадачи", 400);
            return;
        }
        try {
            final Subtask subtask = taskManager.getSubtaskById(subtaskId.get());
            final String response = gson.toJson(subtask);
            taskManager.removeSubtask(subtaskId.get());
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            sendText(exchange, response, 200);
        } catch (IllegalArgumentException e) {
            sendText(exchange, e.getMessage(), 404);
        } catch (ManagerSaveException e) {
            sendText(exchange, e.getMessage(), 500);
        }
    }
}
