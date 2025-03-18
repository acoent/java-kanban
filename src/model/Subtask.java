package model;

import java.util.Objects;

public class Subtask extends Task {
    private final Integer parentEpicId;

    public Subtask(Integer parentEpicId, String name, String description) {
        super(name, description);
        this.parentEpicId = parentEpicId;
    }

    public Subtask(Integer parentEpicId, String name, String description, Status status) {
        super(name, description, status);
        this.parentEpicId = parentEpicId;
    }

    public Integer getParentEpicId() {
        return parentEpicId;
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
        return String.format("%d,%s,%s,%s,%s,%d", getId(), Type.SUBTASK, getTaskName(), getStatus(), getDescription(), getParentEpicId());
    }
}
