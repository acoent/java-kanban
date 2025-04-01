package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import exception.TimeIntersectionException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.TreeSet;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String[] path) throws IOException {
        int pathLen = path.length;
        if (path[path.length - 1].equals("tasks") && pathLen == 2) {
            handleGetTasks(exchange);
        } else if (pathLen == 3) {
            handleGetTaskById(exchange);
        } else {
            sendText(exchange, "Неизвестный запрос.", 400);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        final String response = gson.toJson(taskManager.getTasks());
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        sendText(exchange, response, 200);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        final Optional<Integer> taskId = getId(exchange);
        if (taskId.isEmpty()) {
            sendText(exchange, "Неверный id задачи", 400);
            return;
        }
        try {
            final Task task = taskManager.getTaskById(taskId.get());
            final String response = gson.toJson(task);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            sendText(exchange, response, 200);
        } catch (IllegalArgumentException e) {
            sendText(exchange, e.getMessage(), 404);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String[] path) throws IOException {
        if (path[path.length - 1].equals("tasks") && path.length == 2) {
            handlePostTask(exchange);
        } else {
            sendText(exchange, "Неизвестный запрос.", 400);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        final Task task = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Task.class);
        if (task == null) {
            sendText(exchange, "Некоректный запрос.", 400);
            return;
        }
        if (task.getId() == 0) {
            try {
                taskManager.addTask(task);
                sendText(exchange, "Новая задача сохранена.", 201);
            } catch (TimeIntersectionException e) {
                sendText(exchange, e.getMessage(), 406);
            } catch (ManagerSaveException e) {
                sendText(exchange, e.getMessage(), 500);
            }
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String[] path) throws IOException {
        if (exchange.getRequestURI().getPath().split("/").length == 3) {
            handleDeleteTask(exchange);
        } else {
            sendText(exchange, "Неизвестный запрос.", 400);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        final Optional<Integer> taskIdOpt = getId(exchange);
        if (taskIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный id задачи", 400);
            return;
        }
        try {
            Task task = taskManager.getTaskById(taskIdOpt.get());
            taskManager.removeTask(taskIdOpt.get());
            sendText(exchange, "Задача удалена", 200);
        } catch (ManagerSaveException e) {
            sendText(exchange, e.getMessage(), 500);
        } catch (IllegalArgumentException e) {
            sendText(exchange, e.getMessage(), 404);
        }
    }
}