package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String[] path) throws IOException {
        if (path[path.length - 1].equals("epics") && path.length == 2) {
            handleGetEpics(exchange);
            System.out.println("Запрос GET /epics обработан.");
        } else if (path[path.length - 1].equals("subtasks") && path.length == 4) {
            handleGetSubtasksByParentId(exchange);
            System.out.println("Запрос GET /epics/{id}/subtasks обработан.");
        } else if (path.length == 3) {
            handleGetEpicById(exchange);
            System.out.println("Запрос GET /epics/{id} обработан.");
        } else {
            sendText(exchange, "Неизвестный запрос.", 400);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        final String response = gson.toJson(taskManager.getEpics());
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        sendText(exchange, response, 200);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        final Optional<Integer> epicIdOpt = getId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный id эпика", 400);
            return;
        }
        try {
            final Epic epic = taskManager.getEpicById(epicIdOpt.get());
            final String response = gson.toJson(epic);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            sendText(exchange, response, 200);
        } catch (IllegalArgumentException e) {
            sendText(exchange, e.getMessage(), 404);
        }
    }

    private void handleGetSubtasksByParentId(HttpExchange exchange) throws IOException {
        final Optional<Integer> epicIdOpt = getId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный id эпика", 400);
            return;
        }
        try {
            final String response = gson.toJson(taskManager.getSubtasksByParentId(epicIdOpt.get()));
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            sendText(exchange, response, 200);
        } catch (IllegalArgumentException e) {
            sendText(exchange, e.getMessage(), 404);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String[] path) throws IOException {
        if (path[path.length - 1].equals("epics") && path.length == 2) {
            handlePostEpic(exchange);
            System.out.println("Запрос POST /epics обработан.");
        } else {
            sendText(exchange, "Неизвестный запрос.", 400);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        final Epic newEpic = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Epic.class);
        if (newEpic == null) {
            sendText(exchange, "Отправлен пустой запрос.", 400);
            return;
        }
        if (newEpic.getId() == 0) {
            try {
                final Epic epic = new Epic(newEpic.getTaskName(), newEpic.getDescription());
                taskManager.addEpic(epic);
                sendText(exchange, "Новый эпик сохранен.", 201);
            } catch (ManagerSaveException e) {
                sendText(exchange, e.getMessage(), 500);
            }
        } else {
            try {
                taskManager.epicUpdate(newEpic.getId(), newEpic);
                sendText(exchange, "Эпик с id " + newEpic.getId() + " обновлен.", 201);
            } catch (ManagerSaveException e) {
                sendText(exchange, e.getMessage(), 500);
            }
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String[] path) throws IOException {

        if (path.length == 3) {
            handleDeleteEpic(exchange);
            System.out.println("Запрос DELETE /epics/{id} обработан.");
        } else {
            sendText(exchange, "Неизвестный запрос.", 400);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        final Optional<Integer> epicIdOpt = getId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный id эпика", 400);
            return;
        }
        try {
            final Epic epic = taskManager.getEpicById(epicIdOpt.get());
            final String response = gson.toJson(epic);
            taskManager.removeEpic(epicIdOpt.get());
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            sendText(exchange, response, 200);
        } catch (ManagerSaveException e) {
            sendText(exchange, e.getMessage(), 500);
        } catch (IllegalArgumentException e) {
            sendText(exchange, e.getMessage(), 404);
        }
    }

}
