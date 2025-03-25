package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Duration.ZERO, null);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status, Duration.ZERO, null);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, ArrayList<Integer> subtaskIds) {
        super(name, description, status, Duration.ZERO, null);
        this.subtaskIds = subtaskIds;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    public void removeAllSubtasks() {
        subtaskIds.clear();
    }

    public void addSubtask(Integer subtaskId) {
        if (subtaskId.equals(this.getId())) {
            throw new IllegalArgumentException("Epic cannot be a subtask of itself");
        }
        subtaskIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        return "Epic{" + "epicName='" + getTaskName() + '\'' + ", description='" + getDescription() + '\'' + ", id=" + getId() + ", status=" + getStatus() + ", subtaskIds=" + getSubtaskIds() + '}';
    }

}
