package service;

import model.*;

public class Converter {
    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        Type type = Type.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(epicId, name, description, status);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static String toString(Task task) {
        String type;
        String extraField = "";
        switch (task.getClass().getSimpleName()) {
            case "Epic":
                type = "EPIC";
                Epic epic = (Epic) task;
                StringBuilder sb = new StringBuilder();
                for (Integer subtaskId : epic.getSubtaskIds()) {
                    if (!sb.isEmpty()) {
                        sb.append(",");
                    }
                    sb.append(subtaskId);
                }
                extraField = sb.toString();
                break;
            case "Subtask":
                type = "SUBTASK";
                Subtask subtask = (Subtask) task;
                extraField = String.valueOf(subtask.getParentEpicId());
                break;
            default:
                type = "TASK";
                break;
        }
        return task.getId() + "," + type + "," + task.getTaskName() + "," + task.getStatus() + "," + task.getDescription() + "," + extraField;
    }
}
