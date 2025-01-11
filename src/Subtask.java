import java.util.Objects;

public class Subtask extends Task {
    private Epic parentEpic;

    public Subtask(Epic parentEpic, String name, String description, int id) {
        super(name, description, id);
        this.parentEpic = parentEpic;
    }

    public Epic getParentEpic() {
        return parentEpic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(parentEpic, subtask.parentEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentEpic);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "subtaskName='" + getTaskName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", parentEpicID=" + getParentEpic().getId() +
                '}';
    }
}
