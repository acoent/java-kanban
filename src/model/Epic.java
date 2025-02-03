package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, ArrayList<Integer> subtaskIds) {
        super(name, description, status);
        this.subtaskIds = subtaskIds;
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
    public String toString() {
        return "Epic{" +
                "epicName='" + getTaskName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIds=" + getSubtaskIds() +
                '}';
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
}
