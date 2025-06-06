package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final Integer parentEpicId;

    public Subtask(Integer parentEpicId, String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.parentEpicId = parentEpicId;
    }

    public Subtask(Integer parentEpicId, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.parentEpicId = parentEpicId;
    }

    public Integer getParentEpicId() {
        return parentEpicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(parentEpicId, subtask.parentEpicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentEpicId);
    }

    @Override
    public String toString() {
        return "Subtask{" + "subtaskName='" + getTaskName() + '\'' + ", description='" + getDescription() + '\'' + ", id=" + getId() + ", status=" + getStatus() + ", parentEpicID=" + getParentEpicId() + '}';
    }
}
