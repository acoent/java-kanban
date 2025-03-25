package service;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Converter {
    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        Type type = Type.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = null;
        if (parts.length > 5 && !parts[5].isEmpty()) {
            duration = Duration.parse(parts[5]);
        }
        if (parts.length > 6 && !parts[6].isEmpty()) {
            startTime = LocalDateTime.parse(parts[6]);
        }
        Task task = switch (type) {
            case TASK -> new Task(name, description, status, duration, startTime);
            case EPIC -> new Epic(name, description, status);
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[7]);
                yield new Subtask(epicId, name, description, status, duration, startTime);
            }
        };
        task.setId(id);
        return task;
    }

    public static String toString(Task task) {
        String type;
        String extraField = "";
        String duration = (task.getDuration() != null) ? task.getDuration().toString() : "";
        String startTime = (task.getStartTime() != null) ? task.getStartTime().toString() : "";
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
        return task.getId() + "," + type + "," + task.getTaskName() + "," + task.getStatus() + "," + task.getDescription() + "," + duration + "," + startTime + (extraField.isEmpty() ? "" : "," + extraField);
    }
}